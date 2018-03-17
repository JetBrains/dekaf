@file:Suppress("ConvertSecondaryConstructorToPrimary", "CanBePrimaryConstructorProperty")

package org.jetbrains.dekaf.expectation

import org.opentest4j.AssertionFailedError
import kotlin.reflect.KClass

/**
 * Wrapper around the value under the test.
 * The value is nullable.
 */
class Matter<out T:Any>
{

    /// STATE \\\

    /**
     * The value that is under the test.
     */
    val something: T?

    /**
     * The expected type of the value.
     */
    val declaredType: KClass<*>

    /**
     * Brief info what's verifying.
     */
    val aspect: String?

    /**
     * Whats'expected.
     */
    val expect: String?


    /// CONSTRUCTOR \\\

    constructor(something:    T?,
                declaredType: KClass<*>,
                aspect:       String? = null,
                expect:       String? = null) {
        this.something = something
        this.declaredType = declaredType
        this.aspect = aspect
        this.expect = expect
    }


    fun<R:Any> transform(transformer: (T) -> R): Matter<R> =
            Matter<R>(something    = transformer(thing),
                      declaredType = declaredType,
                      aspect       = aspect,
                      expect       = expect)


    /**
     * Specifies the aspect of testing.
     * When a checking is failed, the given message be presented in the exception.
     */
    fun theAspect(aspect: String): Matter<T> =
            Matter(something    = something,
                   declaredType = declaredType,
                   aspect       = if (this.aspect == null) aspect else this.aspect + ": " + aspect,
                   expect       = expect)

    /**
     * Overrides the expectation text.
     * When a checking failed, this text will be presented in the exception instead of normal expectation text.
     *
     * This function is designed to be used inside checkers, to specify the expectation text once
     * for several consequent inner checks.
     */
    fun expecting(expect: String): Matter<T> =
            Matter(something    = something,
                   declaredType = declaredType,
                   aspect       = aspect,
                   expect       = expect)


    /// FLOW CONTROL \\\

    fun with(aspect: String? = null, expect: String? = null, inner: Matter<T>.() -> Unit): Matter<T> {
        val matter = if (aspect == null && expect == null) this
                     else Matter(something    = something,
                                 declaredType = declaredType,
                                 aspect       = prepareNewAspect(aspect),
                                 expect       = expect ?: this.expect)
        matter.inner()
        return this
    }

    private fun prepareNewAspect(aspect: String?): String? =
        if (this.aspect != null && aspect != null) this.aspect + ": " + aspect
        else aspect ?: this.aspect


    /// SEVERAL MEMBER CHECKERS \\\

    inline fun<reified M: Any> beInstanceOf(): Matter<M> =
            when (something) {
                null -> blame(expect = "an instance of ${M::class.simpleName}",
                              aspect = "Instance class with")
                is M -> Matter(something as M, M::class, aspect, expect)
                else -> blame(expect  = "instance of ${M::class.java.simpleName}",
                              actual  = "instance of ${something.javaClass.simpleName}",
                              details = "The value: " + something.displayString(),
                              aspect  = "Wrong class of instance")
            }

    @Suppress("unchecked_cast")
    fun<M:Any> beInstanceOf(kotlinClass: KClass<M>): Matter<M> =
            when {
                something == null                 -> blame(expect = "an instance of ${kotlinClass.simpleName}",
                                                           aspect = "Instance class with")
                kotlinClass.isInstance(something) -> Matter(something as M, kotlinClass, aspect, expect)
                else                              -> blame(expect  = "instance of ${kotlinClass.simpleName}",
                                                           actual  = "instance of ${something.javaClass.kotlin.simpleName}",
                                                           details = "The value: " + something.displayString(),
                                                           aspect  = "Wrong class of instance")
            }

    @Suppress("unchecked_cast")
    fun<M:Any> beInstanceOf(javaClass: Class<M>): Matter<M> =
            when {
                something == null                               -> blame(expect = "an instance of ${javaClass.simpleName}",
                                                                         aspect = "Instance class with")
                javaClass.isAssignableFrom(something.javaClass) -> Matter(something as M, javaClass.kotlin, aspect, expect)
                else                                            -> blame(expect  = "instance of ${javaClass.simpleName}",
                                                                         actual  = "instance of ${something.javaClass.simpleName}",
                                                                         details = "The value: " + something.displayString(),
                                                                         aspect  = "Wrong class of instance")
            }

    val beNotNull: Matter<T>
        get() = if (something == null) blame(expect = "Non-null value of type $declaredType")
                else this

    val beNull: Unit
        get()  {
            if (something == null) return
            else blame(expect = "Null value of type $declaredType",
                       actual = displayText)
        }

    fun satisfy(predicate: (x: T) -> Boolean): Matter<T> =
            if (predicate(thing)) this
            else blame(expect = "satisfying the specified predicate")

    fun satisfy(expect: String, predicate: (x: T) -> Boolean): Matter<T> =
            if (predicate(thing)) this
            else blame(expect = expect)



    /// HELPER FUNCTIONS FOR CHECKERS \\\

    val thing: T
        get() = something ?: blame(expect = "Non-null value of type: $declaredType")

    fun thing(expect: String): T =
            something ?: blame(expect = expect)

    val displayText: String
        get() = something.displayText()


    /// BLAME \\\

    fun blame(expect: String?   = null,
              actual: String?   = null,
              details: String?  = null,
              aspect: String?   = null,
              diff: Boolean     = false,
              cause: Throwable? = null): Nothing
    {
        val aspectText = aspect ?: if (diff) "Difference" else null
        val actualText = actual ?: if (expect != null) displayText else null
        val expectText = this.expect ?: expect

        // prepare the message
        val b = StringBuilder()
        b.append("\nFAIL")
        if (this.aspect != null) b.append(": ").append(this.aspect)
        if (aspectText != null) b.append(": ").append(aspectText)
        if (cause?.message != null) b.append(": ").append(cause.message)
        b.append('\n')
        if (actualText != null) b.append("ACTUAL: ").append(actualText).append('\n')
        if (expectText != null) b.append("EXPECTED: ").append(expectText).append('\n')
        if (details != null) b.append("DETAILS: ").append(details).append('\n')
        val message = b.toString()

        // throw it
        if (diff) {
            throw DiffAssertionFailedError(message, expect, actual, cause)
        }
        else {
            throw BasicAssertionFailedError(message, cause)
        }
    }

}




////// FORMERS \\\\\\


val <reified T:Any> T?.must: Matter<T>
    inline get() = Matter(this, T::class)






////// EXCEPTIONS \\\\\\


class BasicAssertionFailedError: AssertionFailedError
{
    constructor(message: String?, cause: Throwable?)
            : super(message, cause)
}


class DiffAssertionFailedError: AssertionFailedError
{
    constructor(message: String?, expected: Any?, actual: Any?, cause: Throwable?)
            : super(message, expected, actual, cause)
}


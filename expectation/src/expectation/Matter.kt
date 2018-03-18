@file:Suppress("ConvertSecondaryConstructorToPrimary", "CanBePrimaryConstructorProperty")

package org.jetbrains.dekaf.expectation

import org.opentest4j.AssertionFailedError
import kotlin.reflect.KClass

/**
 * Wrapper around the value under the test.
 * The value is nullable.
 */
open class Matter<out T:Any>
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


    internal open fun copy(aspect: String? = this.aspect, expect: String? = this.expect): Matter<T> =
            Matter(this.something, this.declaredType, aspect, expect)


    fun<R:Any> transform(transformer: (T) -> R): Matter<R> =
            Matter<R>(something    = transformer(thing),
                      declaredType = declaredType,
                      aspect       = aspect,
                      expect       = expect)






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



    /// HELPER FUNCTIONS FOR CHECKERS \\\

    val thing: T
        get() = something ?: blame(expect = "Non-null value of type: $declaredType")

    fun thing(expect: String): T =
            something ?: blame(expect = expect)

    val displayText: String
        get() = something.displayText()

    fun prepareNewAspect(aspect: String?): String? =
            if (this.aspect != null && aspect != null) this.aspect + ": " + aspect
            else aspect ?: this.aspect


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



class MultiMatter<out E: Any, out T: Any> : Matter<T>
{
    val elements: List<E>

    constructor(something: T?,
                declaredType: KClass<*>,
                elements: List<E>?,
                aspect: String? = null,
                expect: String? = null)
            : super(something, declaredType, aspect, expect)
    {
        this.elements = elements ?: emptyList()
    }


    override fun copy(aspect: String?, expect: String?): MultiMatter<E,T> =
            MultiMatter(this.something, this.declaredType, elements, aspect, expect)

}





////// FORMERS \\\\\\


val <reified T:Any> T?.must: Matter<T>
    inline get() = Matter(this, T::class)

val <reified E:Any> Array<out E>?.must: MultiMatter<E, Array<out E>>
    inline get() = MultiMatter(this, Array<E>::class, this?.asList())

val <reified E:Any, reified T:List<E>> T?.must: MultiMatter<E,T>
    inline get() = MultiMatter(this, T::class, this)

val <reified E:Any, reified T:Iterable<E>> T?.must: MultiMatter<E,T>
    inline get() = MultiMatter(this, T::class, this?.toList())


val ByteArray.must: MultiMatter<Byte, ByteArray>
    get() = MultiMatter(this, ByteArray::class, this.explode())

val ShortArray.must: MultiMatter<Short, ShortArray>
    get() = MultiMatter(this, ShortArray::class, this.explode())

val IntArray.must: MultiMatter<Int, IntArray>
    get() = MultiMatter(this, IntArray::class, this.explode())

val LongArray.must: MultiMatter<Long, LongArray>
    get() = MultiMatter(this, LongArray::class, this.explode())

val FloatArray.must: MultiMatter<Float, FloatArray>
    get() = MultiMatter(this, FloatArray::class, this.explode())

val DoubleArray.must: MultiMatter<Double, DoubleArray>
    get() = MultiMatter(this, DoubleArray::class, this.explode())





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


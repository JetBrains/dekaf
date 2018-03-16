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
    val declaredType: KClass<out T>

    /**
     * Brief info what's verifying.
     */
    val aspect: String?



    /// CONSTRUCTOR \\\

    constructor(something: T?, declaredType: KClass<out T>, aspect: String? = null) {
        this.something = something
        this.declaredType = declaredType
        this.aspect = aspect
    }


    /// HELPER FUNCTIONS \\\

    val thing: T
        get() = something ?: blame("Value of type: $declaredType must not be null")

    fun thing(check: String): T =
        something ?: blame("$check: Value of type: $declaredType must not be null")

    val displayString: String
        get() = something.displayString()
    

    /// BLAME \\\

    fun blame(check:   String?,
              expect:  String?    = null,
              actual:  String?    = null,
              details: String?    = null,
              diff:    Boolean    = false,
              cause:   Throwable? = null): Nothing
    {
        val actualText = actual ?: if (expect != null) displayString else null
        val checkText = check ?: if (diff) "Difference" else null

        // prepare the message
        val b = StringBuilder()
        b.append("\nFAIL")
        if (aspect != null) b.append(": ").append(aspect)
        if (checkText != null) b.append(": ").append(checkText)
        if (cause?.message != null) b.append(": ").append(cause.message)
        b.append('\n')
        if (actualText != null) b.append("ACTUAL: ").append(actualText).append('\n')
        if (expect != null) b.append("EXPECTED: ").append(expect).append('\n')
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




    /// SEVERAL MEMBER CHECKERS \\\

    inline fun<reified M: Any> beInstanceOf(): Matter<M> =
        if (something == null) blame(check = "Instance class check",
                                     expect = "an instance of ${M::class.java.simpleName}")
        else if (this is M) Matter(something as M, M::class, aspect)
        else blame(check   = "Wrong class of instance",
                   actual  = "instance of ${something.javaClass.simpleName}",
                   expect  = "instance of ${M::class.java.simpleName}",
                   details = "The value: " + something.displayString())


    val beNotNull: Matter<T>
        get() = if (something == null) blame("Value of type $declaredType must not be null)")
                else this

    val beNull: Unit
        get()  {
            if (something == null) return
            else blame(check = "Value (type $declaredType) must be null",
                       actual = displayString)
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


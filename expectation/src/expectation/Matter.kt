@file:Suppress("ConvertSecondaryConstructorToPrimary", "CanBePrimaryConstructorProperty")

package org.jetbrains.dekaf.expectation

import kotlin.reflect.KClass

/**
 * Wrapper around the value under the test.
 * The value is nullable.
 */
sealed class OptionalMatter<T:Any> (type: KClass<T>)
{
    /// STATE ACCESSORS \\\

    /**
     * The value that is under the test.
     */
    abstract val thing: T?

    /**
     * The expected type of the value.
     */
    val declaredType: KClass<out T> = type


    /// SEVERAL MEMBER CHECKERS \\\

    inline fun<reified M: T> beInstanceOf(): Matter<M> {
        if (thing == null) blameNull("an instance of ${M::class.java.simpleName}")
        else if (this is M) { /* OK */ }
        else blameActualExpectValue("Wrong class of instance",
                                    "instance of ${thing!!.javaClass.simpleName}",
                                    "instance of ${M::class.java.simpleName}",
                                    thing!!.displayString())
        return Matter(thing as M, M::class)
    }


    /// INTERNAL STUFF \\\

    internal abstract fun m(expectationText: String): Matter<T>

}


/**
 * Wrapper around the value under the test.
 * The value is not null.
 */
class Matter<T:Any> : OptionalMatter<T>
{
    override val thing: T

    constructor(thing: T, declaredType: KClass<T>) : super(declaredType) {
        this.thing = thing
    }

    override fun toString(): String {
        return "${thing.displayString()} (declared type: ${declaredType.simpleName})"
    }


    /// INTERNAL STUFF \\\

    override fun m(expectationText: String): Matter<T> = this
}


/**
 * Wrapper around the null value under the test.
 * The value is always null.
 */
class NullMatter<T:Any>(declaredType: KClass<T>) : OptionalMatter<T>(declaredType)
{
    override val thing: T? = null

    override fun toString(): String {
        return "NULL (declared type: ${declaredType.simpleName})"
    }

    /// INTERNAL STUFF \\\

    override fun m(expectationText: String): Nothing =
        blameNull(expectationText)
    
}



val <reified T:Any> T.must: Matter<T>
    inline get() = Matter(this, T::class)


val <reified T:Any> T?.must: OptionalMatter<T>
    inline get() =
        if (this != null) Matter(this, T::class)
        else NullMatter(T::class)



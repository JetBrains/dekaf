@file:JvmName("KoExpectations")
@file:Suppress("unused_parameter", "platform_class_mapped_to_kotlin")

package org.jetbrains.dekaf.assertions

import org.junit.jupiter.api.Assertions
import org.opentest4j.AssertionFailedError
import kotlin.math.sign
import kotlin.reflect.KClass


/// ASPECT MARKERS \\\

sealed class KoAssertAspect {
    override fun toString(): String = this.javaClass.simpleName
}

object IsNull         : KoAssertAspect()
object IsNotNull      : KoAssertAspect()
object IsTrue         : KoAssertAspect()
object IsFalse        : KoAssertAspect()
object IsFalseOrNull  : KoAssertAspect()
object IsEmptyOrNull  : KoAssertAspect()
object IsEmpty        : KoAssertAspect()
object IsNotEmpty     : KoAssertAspect()



/// ANY \\\

infix fun Any?.expected(aspect: IsNull) {
    if (this != null) fail("Got \"$this\" when expected null")
}

inline infix fun<reified A:Any> A?.expected(aspect: IsNotNull): A {
    if (this == null) {
        fail("Got null when expected a non-null instance of ${A::class.java.displayName}")
    }
    return this
}

infix fun<A> A?.expected(value: A): A {
    assert(value !is KoAssertAspect) {"Wrong resolving!"}
    if (this == null) failDiff(null, value)
    if (this != value) failDiff(this, value)
    return this
}

infix fun<A> A?.expectedSameAs(value: A): A {
    if (this == null) failDiff(null, value)
    if (this !== value) fail("Got $this when expected the reference to $value")
    return this
}

infix fun<A:Any> A?.expectedNotSameAs(value: A?): A? {
    when {
        this == null && value == null -> fail("Got two nulls when expected two different instances")
        this == null                  -> return null
        this === value                -> {
            val message = "Got references to the same object \"$this\" when expected two different instances of class ${this.javaClass.displayName}"
            fail(message)
        }
    }
    return this
}

infix fun<A> Any?.expectedClass(expectedClass: java.lang.Class<out A>): A {
    if (this != null) {
        val actualClass = this.javaClass
        if (actualClass == expectedClass || expectedClass.isAssignableFrom(actualClass)) {
            @Suppress("unchecked_cast")
            return this as A
        }
        else {
            val message = "Got an instance of ${actualClass.displayName} when expected one of ${expectedClass.displayName} (the value: \"$this\")"
            fail(message)
        }
    }
    else {
        fail("Got null when expected an instance of ${expectedClass.displayName}")
    }
}

infix fun<A:Any> Any?.expectedClass(expectedClass: KClass<out A>): A {
    if (this != null) {
        val actualClass: KClass<out Any> = this::class
        if (actualClass == expectedClass || expectedClass.java.isAssignableFrom(actualClass.java)) {
            @Suppress("unchecked_cast")
            return this as A
        }
        else {
            val message = "Got an instance of ${actualClass.displayName} when expected one of ${expectedClass.displayName} (the value: \"$this\")"
            fail(message)
        }
    }
    else {
        fail("Got null when expected an instance of ${expectedClass.displayName}")
    }
}

infix fun<E> Any?.expectedArrayOfClass(expectedElementClass: java.lang.Class<out E>): Array<E> {
    if (this != null) {
        val actualClass = this.javaClass
        if (!actualClass.isArray) {
            fail("Got an instance of ${actualClass.displayName} when expected an array of ${expectedElementClass.displayName}")
        }

        val actualElementClass = actualClass.componentType
        if (expectedElementClass.isAssignableFrom(actualElementClass)) {
            @Suppress("unchecked_cast")
            return this as Array<E>
        }
        else {
            fail("Got an array of ${actualElementClass.displayName} when expected an array of ${expectedElementClass.displayName}")
        }
    }
    else {
        fail("Got null when expected an array of ${expectedElementClass.displayName}")
    }
}


val KClass<*>.displayName: String
        get() = if (".kotlin." in this.java.name) this.java.name else this.java.simpleName

val Class<*>.displayName: String
        get() = if (".kotlin." in this.name) this.name else this.simpleName



/// BOOLEAN \\\

infix fun Boolean?.expected(aspect: IsTrue) {
    if (this == null) failDiff("Got null when expected True", null, true)
    if (this != true) failDiff("Got $this when expected True", this, true)
}

infix fun Boolean?.expected(aspect: IsFalse) {
    if (this == null) failDiff("Got null when expected False", null, false)
    if (this != false) failDiff("Got $this when expected False", this, false)
}

infix fun Boolean?.expected(aspect: IsFalseOrNull) {
    if (this == true) fail("Got $this when expected False or null")
}


/// STRINGS \\\

infix fun CharSequence?.expected(aspect: IsEmptyOrNull) {
    if (this != null && this.isNotEmpty()) fail("Got $this when expected an empty string or null")
}

infix fun CharSequence?.expected(aspect: IsEmpty) {
    if (this == null) fail("Got null when expected an empty string")
    if (this.isNotEmpty()) fail("Got $this when expected an empty string or null")
}

infix fun CharSequence?.expected(aspect: IsNotEmpty) {
    if (this == null) fail("Got null when expected a non-empty string")
    if (this.isEmpty()) fail("Got an empty string when expected a non-empty one")
}


/// ARRAYS AND COLLECTIONS \\\

infix fun<E,C:Collection<E>> C?.expected(aspect: IsNotEmpty): C {
    if (this != null) {
        if (this.isEmpty()) fail("Got an empty ${this.javaClass.displayName} when expected a non-empty one")
        return this
    }
    else {
        fail("Got null when expected a non-empty collection")
    }
}

infix fun<E,C:Collection<E>> C?.expected(aspect: IsEmpty): C {
    if (this != null) {
        if (!this.isEmpty()) {
            when (this) {
                is Set<*>  -> fail("Got a non-empty set $this when expected an empty one")
                is List<*> -> fail("Got a non-empty list $this when expected an empty one")
                else -> fail("Got a non-empty collection $this when expected an empty one")
            }
        }
        return this
    }
    else {
        fail("Got null when expected an empty collection")
    }
}

infix fun<E> Array<out E>?.expected(array: Array<out E>) {
    if (this == null || this.isEmpty()) fail("Got ${this.displayString()} when expected ${array.displayString()}")
    val n1 = this.size
    val n2 = array.size
    if (n1 != n2) failDiff("Arrays have different sizes: got ${this.displayString()} when expected ${array.displayString()}", this, array)
    val eq = areArraysEqual(this, array)
    if (!eq) failDiff("Arrays are different: got ${this.displayString()} when expected ${array.displayString()}", this, array)
}

private fun <T> areArraysEqual(a: Array<out T>, b: Array<out T>): Boolean {
    if (a === b) return true
    val n = a.size
    if (b.size != n) return false
    for (i in 0 until n) if (!areValuesEqual(a[i], b[i])) return false
    return true
}

private fun areValuesEqual(a: Any?, b: Any?): Boolean =
        when {
            a === b                        -> true
            a === null || b === null       -> false
            a is Array<*> && b is Array<*> -> areArraysEqual(a, b)
            else                           -> a == b
        }

infix fun<E> List<E>?.expected(list: List<E>) {
    if (this == null || this.isEmpty()) fail("Got ${this.displayString()} when expected ${list.displayString()}")
    val n1 = this.size
    val n2 = list.size
    if (n1 != n2) failDiff("Lists have different sizes: got ${this.displayString()} when expected ${list.displayString()}", this, list)
    var d = false
    for (i in 0..n1-1) if (this[i] != list[i]) { d = true; break }
    if (d) failDiff("Lists are different: got ${this.displayString()} when expected ${list.displayString()}", this, list)
}


infix fun ByteArray?.expected(array: ByteArray) {
    if (this == null || this.isEmpty()) fail("Got ${this.displayString()} when expected ${array.displayString()}")
    val n1 = this.size
    val n2 = array.size
    if (n1 != n2) failDiff("Arrays have different sizes: got ${this.displayString()} when expected ${array.displayString()}", this, array)
    var d = false
    for (i in 0..n1-1) if (this[i] != array[i]) { d = true; break }
    if (d) failDiff("Arrays are different: got ${this.displayString()} when expected ${array.displayString()}", this, array)
}

infix fun ShortArray?.expected(array: ShortArray) {
    if (this == null || this.isEmpty()) fail("Got ${this.displayString()} when expected ${array.displayString()}")
    val n1 = this.size
    val n2 = array.size
    if (n1 != n2) failDiff("Arrays have different sizes: got ${this.displayString()} when expected ${array.displayString()}", this, array)
    var d = false
    for (i in 0..n1-1) if (this[i] != array[i]) { d = true; break }
    if (d) failDiff("Arrays are different: got ${this.displayString()} when expected ${array.displayString()}", this, array)
}

infix fun IntArray?.expected(array: IntArray) {
    if (this == null || this.isEmpty()) fail("Got ${this.displayString()} when expected ${array.displayString()}")
    val n1 = this.size
    val n2 = array.size
    if (n1 != n2) failDiff("Arrays have different sizes: got ${this.displayString()} when expected ${array.displayString()}", this, array)
    var d = false
    for (i in 0..n1-1) if (this[i] != array[i]) { d = true; break }
    if (d) failDiff("Arrays are different: got ${this.displayString()} when expected ${array.displayString()}", this, array)
}

infix fun LongArray?.expected(array: LongArray) {
    if (this == null || this.isEmpty()) fail("Got ${this.displayString()} when expected ${array.displayString()}")
    val n1 = this.size
    val n2 = array.size
    if (n1 != n2) failDiff("Arrays have different sizes: got ${this.displayString()} when expected ${array.displayString()}", this, array)
    var d = false
    for (i in 0..n1-1) if (this[i] != array[i]) { d = true; break }
    if (d) failDiff("Arrays are different: got ${this.displayString()} when expected ${array.displayString()}", this, array)
}


/// NUMBERS \\\

infix fun<N: Comparable<N>> N?.expectedGreaterThan(than: N): N {
    if (this == null) fail("Got null when expected a value greater than $than")
    val c = this.compareTo(than)
    when (c.sign) {
        +1   -> return this
        0    -> fail("Got $this when expected greater")
        -1   -> fail("Got $this when expected greater than $than")
        else -> error("Just for compiler")
    }
}

infix fun<N: Number> N?.expectedGreaterThan(than: Int): N
    = expectedGreaterThan(than.toLong())

infix fun<N: Number> N?.expectedGreaterThan(than: Long): N {
    if (this == null) fail("Got null when expected a value greater than $than")
    val v = this.toLong()
    when {
        v > than  -> return this
        v == than -> fail("Got $this when expected greater")
        else      -> fail("Got $this when expected greater than $than")
    }
}

infix fun Int.expectedGreaterThan(than: Int): Int {
    when {
        this > than  -> return this
        this == than -> fail("Got $this when expected greater")
        else         -> fail("Got $this when expected greater than $than")
    }
}

infix fun Long.expectedGreaterThan(than: Long): Long {
    when {
        this > than  -> return this
        this == than -> fail("Got $this when expected greater")
        else         -> fail("Got $this when expected greater than $than")
    }
}



///  AUXILIARY FUNCTIONS \\\




fun fail(message: String): Nothing =
        throw AssertionFailedError(message)

fun failDiff(actual: Any?, expected: Any?): Nothing =
        throw AssertionFailedError("Got $actual when expected $expected", expected, actual)

fun failDiff(message: String, actual: Any?, expected: Any?): Nothing =
        throw AssertionFailedError(message, expected, actual)


/// EXCEPTIONS \\\

inline fun <reified E: Exception> expectedException(crossinline block: () -> Unit) {
    Assertions.assertThrows(E::class.java) { block.invoke() }
}




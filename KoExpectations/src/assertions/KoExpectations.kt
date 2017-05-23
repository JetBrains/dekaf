@file:JvmName("KoExpectations")
@file:Suppress("unused_parameter", "platform_class_mapped_to_kotlin")

package org.jetbrains.dekaf.assertions

import org.junit.jupiter.api.Assertions.fail
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
        fail("Got null when expected a non-null instance of ${A::class.java.simpleName}")
    }
    return this!!
}

infix fun<A> A?.expected(value: A): A {
    assert(value !is KoAssertAspect) {"Wrong resolving!"}
    if (this == null) fail("Got null when expected $value")
    if (this != value) fail("Got $this when expected $value")
    return this!!
}

infix fun<A> A?.expectedSameAs(value: A): A {
    if (this == null) fail("Got null when expected $value")
    if (this !== value) fail("Got $this when expected the reference to $value")
    return this!!
}

infix fun<A:Any> A?.expectedNotSameAs(value: A?): A? {
    when {
        this == null && value == null -> fail("Got two nulls when expected two different instances")
        this == null                  -> return null
        this === value                -> {
            val message = "Got references to the same object \"$this\" when expected two different instances of class ${this.javaClass.simpleName}"
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
            val message = "Got an instance of ${actualClass.simpleName} when expected one of ${expectedClass.simpleName} (the value: \"$this\")"
            fail(message)
            throw RuntimeException() // never reached, just for compiler
        }
    }
    else {
        fail("Got null when expected an instance of ${expectedClass.simpleName}")
        throw RuntimeException() // never reached, just for compiler
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
            val message = "Got an instance of ${actualClass.simpleName} when expected one of ${expectedClass.simpleName} (the value: \"$this\")"
            fail(message)
            throw RuntimeException() // never reached, just for compiler
        }
    }
    else {
        fail("Got null when expected an instance of ${expectedClass.simpleName}")
        throw RuntimeException() // never reached, just for compiler
    }
}

infix fun<E> Any?.expectedArrayOfClass(expectedElementClass: java.lang.Class<out E>): Array<E> {
    if (this != null) {
        val actualClass = this.javaClass
        if (!actualClass.isArray) {
            fail("Got an instance of ${actualClass.simpleName} when expected an array of ${expectedElementClass.simpleName}")
            throw RuntimeException() // never reached, just for compiler
        }

        val actualElementClass = actualClass.componentType
        if (expectedElementClass.isAssignableFrom(actualElementClass)) {
            @Suppress("unchecked_cast")
            return this as Array<E>
        }
        else {
            fail("Got an array of ${actualElementClass.simpleName} when expected an array of ${expectedElementClass.simpleName}")
            throw RuntimeException() // never reached, just for compiler
        }
    }
    else {
        fail("Got null when expected an array of ${expectedElementClass.simpleName}")
        throw RuntimeException() // never reached, just for compiler
    }
}




/// BOOLEAN \\\

infix fun Boolean?.expected(aspect: IsTrue) {
    if (this == null) fail("Got null when expected True")
    if (this != true) fail("Got $this when expected True")
}

infix fun Boolean?.expected(aspect: IsFalse) {
    if (this == null) fail("Got null when expected False")
    if (this != false) fail("Got $this when expected False")
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
    if (this!!.isNotEmpty()) fail("Got $this when expected an empty string or null")
}

infix fun CharSequence?.expected(aspect: IsNotEmpty) {
    if (this == null) fail("Got null when expected a non-empty string")
    if (this!!.isEmpty()) fail("Got an empty string when expected a non-empty one")
}


/// ARRAYS AND COLLECTIONS \\\

infix fun<E,C:Collection<E>> C?.expected(aspect: IsNotEmpty): C {
    if (this != null) {
        if (this.isEmpty()) fail("Got an empty ${this.javaClass.simpleName} when expected a non-empty one")
        return this
    }
    else {
        fail("Got null when expected a non-empty collection")
        throw RuntimeException() // never reached, just for compiler
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
        throw RuntimeException() // never reached, just for compiler
    }
}








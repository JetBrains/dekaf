@file:JvmName("KoExpectations")
@file:Suppress("unused_parameter", "platform_class_mapped_to_kotlin")

package org.jetbrains.dekaf.assertions

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.fail


/// ASPECT MARKERS \\\

sealed class KoAssertAspect

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
    assertNull(this)
}

infix fun<A> A?.expected(aspect: IsNotNull): A {
    assertNull(this)
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

infix fun<A> Any?.expectedClass(expectedClass: java.lang.Class<A>): A {
    if (this != null) {
        val actualClass = this.javaClass
        if (expectedClass.isAssignableFrom(actualClass)) {
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








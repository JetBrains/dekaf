@file:JvmName("KoExpectations")
@file:Suppress("unused_parameter")

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
    if (this == null) fail("Got null when expected $value")
    if (this != value) fail("Got $this when expected $value")
    return this!!
}

infix fun<A> A?.expectedSameAs(value: A): A {
    if (this == null) fail("Got null when expected $value")
    if (this !== value) fail("Got $this when expected the reference to $value")
    return this!!
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









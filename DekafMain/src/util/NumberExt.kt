@file:JvmName("NumberExt")
@file:Suppress("nothing_to_inline")

package org.jetbrains.dekaf.util


inline infix fun Int.ifZero(then: Int) = if (this != 0) this else then

inline infix fun Int.ifNegative(then: Int) = if (this >= 0) this else then


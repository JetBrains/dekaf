@file:JvmName("NumberExt")
@file:Suppress("nothing_to_inline")

package org.jetbrains.dekaf.util


inline infix fun Int.ifZero(then: Int) = if (this != 0) this else then

inline infix fun Int.ifNegative(then: Int) = if (this >= 0) this else then


fun Int.toFixString(digits: Int): String {
    val b = StringBuilder(digits+2)
    b.append(this)
    val p = if (this < 0) 1 else 0
    while (b.length - p < digits) b.insert(p, '0')
    return b.toString()
}

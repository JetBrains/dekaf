@file:JvmName("NumberExt")
@file:Suppress("nothing_to_inline", "RemoveRedundantBackticks", "ObjectPropertyName")

package org.jetbrains.dekaf.util


const val `0`:   Byte = 0
const val `1`:   Byte = 1
const val `2`:   Byte = 2
const val `3`:   Byte = 3
const val `4`:   Byte = 4
const val `127`: Byte = 127

const val `-1`:   Byte = -1
const val `-2`:   Byte = -2
const val `-127`: Byte = -127
const val `-128`: Byte = -128


const val `0s`:      Short = 0
const val `1s`:      Short = 1
const val `2s`:      Short = 2
const val `32767s`:  Short = 32767

const val `-1s`:     Short = -1
const val `-2s`:     Short = -2
const val `-32767s`: Short = -32767
const val `-32768s`: Short = -32768



inline infix fun Int.ifZero(then: Int) = if (this != 0) this else then

inline infix fun Int.ifNegative(then: Int) = if (this >= 0) this else then


fun Int.toFixString(digits: Int): String {
    val b = StringBuilder(digits+2)
    b.append(this)
    val p = if (this < 0) 1 else 0
    while (b.length - p < digits) b.insert(p, '0')
    return b.toString()
}


/**
 * Gets unsigned value (from 0 to 255) of this byte.
 * @see java.lang.Byte.toUnsignedInt
 */
inline fun Byte.toUnsignedInt(): Int = java.lang.Byte.toUnsignedInt(this)

/**
 * Gets unsigned value (from 0 to 65535) of this short.
 * @see java.lang.Short.toUnsignedInt
 */
inline fun Short.toUnsignedInt(): Int = java.lang.Short.toUnsignedInt(this)


inline infix fun Byte.and(that: Byte): Byte = (this.toUnsignedInt() and that.toUnsignedInt()).toByte()
inline infix fun Byte.or(that: Byte): Byte = (this.toUnsignedInt() or that.toUnsignedInt()).toByte()


inline infix fun Short.and(that: Short): Short = (this.toUnsignedInt() and that.toUnsignedInt()).toShort()
inline infix fun Short.or(that: Short): Short = (this.toUnsignedInt() or that.toUnsignedInt()).toShort()

inline infix fun Byte.hasBit(that: Byte): Boolean = (this.toUnsignedInt() and that.toUnsignedInt()) != 0


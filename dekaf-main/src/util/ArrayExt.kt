@file:JvmName("ArrayExt")

package org.jetbrains.dekaf.util


/**
 * Splits the given array to several ones,
 * where the size of each one is <c>sliceSize</c>,
 * excluding the last one that can be shorter.
 */
infix fun <E: Any?> Array<out E>.chopBy(sliceSize: Int): Array<out Array<out E>> =
    ArrayUtil.chopArrayBy<E>(this, sliceSize)


/**
 * Splits the given array to several ones,
 * where the size of each one is exactly <c>sliceSize</c>.
 * If the number of origin array doesn't divide on the slice size,
 * the last slice is padded with nulls.
 */
infix fun <E: Any?> Array<out E>.chopAndPadBy(sliceSize: Int): Array<out Array<out E?>> =
    ArrayUtil.chopAndPadArrayBy<E>(this, sliceSize, null)


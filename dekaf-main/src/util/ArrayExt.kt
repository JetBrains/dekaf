@file:JvmName("ArrayExt")

package org.jetbrains.dekaf.util


fun <E: Any?> Array<out E>.chopBy(sliceSize: Int): Array<out Array<out E?>> =
    ArrayUtil.chopArrayBy<E>(this, sliceSize, null)

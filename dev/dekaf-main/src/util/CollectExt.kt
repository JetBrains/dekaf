@file:JvmName("CollectExt")
package org.jetbrains.dekaf.util

import java.lang.Math.min
import java.util.*


/**
 * Return an immutable (shallow) copy of the given collection.
 * @param collection   the collection to copy.
 * *
 * @param <T>          the type of elements.
 * *
 * @return             just created copy.
</T> */
fun <T> listCopy(collection: Collection<T>?): List<T> {
    if (collection == null || collection.isEmpty()) {
        return emptyList()
    }
    else {
        val n = collection.size
        if (n == 1) {
            return listOf(collection.iterator().next())
        }
        else {
            return Collections.unmodifiableList(ArrayList(collection))
        }
    }
}


fun arrayToString(array: Array<*>?, delimiter: String): String {
    if (array == null) return ""
    val n = array.size
    when (n) {
        0    -> return ""
        1    -> return array[0].toString()
        else -> {
            val b = StringBuilder()
            b.append(array[0].toString())
            for (i in 1..n - 1) b.append(delimiter).append(array[i])
            return b.toString()
        }
    }
}


@JvmOverloads
fun collectionToString(collection: Iterable<*>?,
                       delimiter: String?,
                       prefix: String? = null,
                       suffix: String? = null,
                       empty: String? = null): String {
    if (collection == null) {
        return empty ?: ""
    }

    val b = StringBuilder()
    for (`object` in collection) {
        if (b.length == 0) {
            if (prefix != null) b.append(prefix)
        }
        else {
            if (delimiter != null) b.append(delimiter)
        }

        b.append(`object`)
    }

    if (b.length > 0) {
        if (suffix != null) b.append(suffix)
    }
    else {
        if (empty != null) b.append(empty)
    }

    return b.toString()
}


/**
 * Splits the given list to several ones,
 * where the size of each one is <c>sliceSize</c>,
 * excluding the last one that can be shorter.
 *
 * @receiver a list to split.
 * @param sliceSize length of slices.
 * @return   list of list slices — every inner list is a sublist of the original one;
 *           if the original list is not longer that desired slices,
 *           the single-element list of the original instance is returned.
 *           The returned list can be unmodifiable.
 */
infix fun <T: Any?> List<T>.chopBy(sliceSize: Int): List<List<T>> {
    val n = this.size
    if (n <= sliceSize) return Collections.singletonList(this)
    if (sliceSize <= 0) throw IllegalArgumentException("The slice size must be positive but it is $sliceSize")
    val result = ArrayList<List<T>>(n / sliceSize + if (n % sliceSize == 0) 0 else 1)
    for (offset in 0 until n step sliceSize) {
        val slice = this.subList(offset, min(offset+sliceSize, n))
        result.add(slice)
    }
    return result
}


/**
 * Splits the given list to several ones,
 * where the size of each one is exactly <c>sliceSize</c>.
 * If the number of origin list doesn't divide on the slice size,
 * the last slice is padded with nulls.
 *
 * @receiver a list to split.
 * @param sliceSize length of slices.
 * @return   list of list slices — every inner list except the last one
 *           is a sublist of the original one;
 *           if the length of the original list is exactly <c>sliceSize</c>,
 *           the single-element list of the original instance is returned.
 *           The returned list can be unmodifiable.
 */
infix fun <T: Any?> List<T>.chopAndPadBy(sliceSize: Int): List<List<T?>> {
    val n = this.size
    if (n == sliceSize) return Collections.singletonList(this)
    if (n == 0) return Collections.emptyList()
    if (sliceSize <= 0) throw IllegalArgumentException("The slice size must be positive but it is $sliceSize")
    val result = ArrayList<List<T?>>(n / sliceSize + if (n % sliceSize == 0) 0 else 1)
    for (offset in 0 until n step sliceSize) {
        val end = offset + sliceSize
        val slice =
                if (end <= n) {
                    this.subList(offset, end)
                }
                else {
                    this.subList(offset, n) + RepeatingList<T?>(null, end - n)
                }
        result.add(slice)
    }
    return result
}

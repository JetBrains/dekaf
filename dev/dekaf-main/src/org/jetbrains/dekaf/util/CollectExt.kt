@file:JvmName("CollectExt")
package org.jetbrains.dekaf.util

import java.lang.Math.min
import java.util.*
import kotlin.collections.HashMap


/**
 * Makes an optimized immutable (shallow) copy of the given collection.
 * @param this@listCopy   the collection to copy.
 * @param <T>             the type of elements.
 * @return                just created copy.
 */
fun <T> Collection<T>?.optimizeToList(): List<T> =
        if (this == null || isEmpty()) {
            emptyList()
        }
        else {
            if (size == 1) listOf(iterator().next())
            else Collections.unmodifiableList(ArrayList(this))
        }


/**
 * Makes an optimized immutable map populated with keys and values made from this elements.
 * @param splitter the function to get a key and a value for each element.
 * @return         the map.
 */
fun <T,K,V> Collection<T>?.optimizeToMap(splitter: (T) -> Pair<K,V>): Map<K,V> =
        if (this == null || isEmpty()) {
            emptyMap()
        }
        else {
            if (size == 1) {
                val element = this.iterator().next()
                val p = splitter(element)
                Collections.singletonMap(p.first, p.second)
            }
            else {
                val map = HashMap<K,V>(size)
                for (element in this) {
                    val p = splitter(element)
                    map.put(p.first, p.second)
                }
                map
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




@Throws(TopologicallySortingException::class)
fun<T:Any> Collection<T>.sortTopologicallyBySetDeferringDependents(dependents: (T) -> Collection<T>?): List<T> {
    val n = size
    if (n == 0) return Collections.emptyList()
    if (n == 1) return Collections.singletonList(this.iterator().next())

    val queue = LinkedHashSet<T>(this)
    val result = LinkedHashSet<T>(n)

    processQueue@
    while (queue.isNotEmpty()) {
        val iterator = queue.iterator()
        do {
            val element = iterator.next()
            val d = dependents(element)
            var ok = true
            if (d != null) {
                for (x: T in d)
                    when (x) {
                        in result -> {}
                        in queue  -> ok = false
                        else      -> throw UnexistentDependencyException(x)
                    }
            }
            if (ok) {
                iterator.remove()
                result.add(element)
                continue@processQueue
            }
        } while (iterator.hasNext())

        throw CyclicDependenciesException()
    }

    return result.toList()
}


/**
 * Thrown when attempted to sort a graph topologically but something went wrong.
 * @see [sortTopologicallyBySetDeferringDependents]
 */
abstract class TopologicallySortingException(message: String): Exception(message)


/**
 * Thrown when attempted to sort a graph topologically but the graph has cycles.
 * @see [sortTopologicallyBySetDeferringDependents]
 */
class CyclicDependenciesException: TopologicallySortingException("Cyclic dependencies")


/**
 * Thrown when attempted to sort a graph topologically but the dependencies contain unknown element.
 * @see [sortTopologicallyBySetDeferringDependents]
 */
class UnexistentDependencyException(val element: Any): TopologicallySortingException("Unexistent dependency: $element")

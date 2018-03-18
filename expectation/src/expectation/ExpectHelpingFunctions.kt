@file:JvmName("ExpectHelpingFunctions")
package org.jetbrains.dekaf.expectation

import java.util.*
import kotlin.collections.ArrayList


/// ARRAYS AND COLLECTIONS \\\

internal data class CheckEntry<out E>
(
        val index:   Int,
        val element: E,
        val failed:  Boolean,
        val remark:  String? = null
)

internal fun<E, T:Any> Matter<T>.checkElements(containerWord: String,
                                               iterator: Iterator<E>,
                                               predictedSize: Int,
                                               predicateDescription: String,
                                               predicate: (E) -> Boolean): Matter<T>
{
    val result: ArrayList<CheckEntry<E>> = if (predictedSize > 0) ArrayList(predictedSize) else ArrayList()
    var index = 0
    var fails = 0
    while (iterator.hasNext()) {
        val element = iterator.next()
        var ok = false
        var remark: String? = null
        try {
            ok = predicate(element)
        }
        catch (e: Throwable) {
            remark = e.message ?: "exception "+e.javaClass.simpleName
        }
        result += CheckEntry(index, element, !ok, remark)
        if (!ok) fails++
        index++
    }

    if (fails > 0) {
        val expectText = "$containerWord when every element: $predicateDescription"
        val b = StringBuilder(index * 60)
        b.append("$containerWord with $index elements where $fails of then failed:")
        for (entry in result) {
            b.append('\n').append('\t')
            b.append(if (entry.failed) "FAIL:\t" else "ok  :\t")
            b.append('[').append(entry.index).append("]\t")
            b.append(entry.element.displayString())
            if (entry.remark != null) b.append(" <- ").append(entry.remark)
        }
        blame(expect = expectText, actual = b.toString())
    }
    else {
        return this
    }
}


@Suppress("unchecked_cast")
internal fun<E> Iterable<E>?.toList(): List<E> {
    val container = this ?: return emptyList()
    if (this is List<E>) return this
    if (this is Array<*>) return Arrays.asList(*(this as Array<out E>))

    val n = this.predictSize()
    if (n == 1) return Collections.singletonList(container.iterator().next())

    val list: ArrayList<E> = if (n > 0) ArrayList(n) else ArrayList()
    for (element in container) list += element
    return list
}


internal fun Iterable<*>.isEmpty(): Boolean =
        when (this) {
            is Collection<*> -> this.isEmpty()
            is Array<*> -> this.size == 0
            else -> !this.iterator().hasNext()
        }

internal fun Iterable<*>.isNotEmpty(): Boolean =
        when (this) {
            is Collection<*> -> !this.isEmpty()
            is Array<*> -> this.size > 0
            else -> this.iterator().hasNext()
        }

internal fun Iterable<*>.countSize(): Int =
        when (this) {
            is Collection<*> -> this.size
            is Array<*> -> this.size
            else -> this.iterator().countSize()
        }

internal fun Iterator<*>.countSize(): Int {
    var n = 0
    while (hasNext()) { n++; next() }
    return n
}


internal fun Iterable<*>?.predictSize(): Int =
        if (this != null)
            when (this) {
                is Collection<*> -> this.size
                is Array<*> -> this.size
                else -> -1
            }
        else 0





internal fun Array<out Any>.elementsText(expectationPrefix: String): String {
    val b = StringBuilder(expectationPrefix)
    for (e in this) b.append('\n').append('\t').append(e.displayString())
    return b.toString()
}

internal fun Collection<Any>.elementsText(expectationPrefix: String): String {
    val b = StringBuilder(expectationPrefix)
    for (e in this) b.append('\n').append('\t').append(e.displayString())
    return b.toString()
}

internal fun<E:Any> Collection<E>.containsAnyOf(vararg elements: E): Boolean {
    if (this.isEmpty()) return false
    if (elements.isEmpty()) return false

    when (this) {
        is Set<*> -> {
            for (element in elements) if (this.contains(element)) return true
        }
        else -> {
            val set = setOf(*elements)
            for (x in this) if (x in set) return true
        }
    }

    return false
}

internal fun<E:Any> Collection<E>.containsAnyOf(elements: Collection<E>): Boolean {
    if (this.isEmpty()) return false
    if (elements.isEmpty()) return false

    when (this) {
        is Set<*> -> {
            for (element in elements) if (this.contains(element)) return true
        }
        else -> {
            val set: Set<E> = elements as? Set<E> ?: elements.toHashSet()
            for (x in this) if (x in set) return true
        }
    }

    return false
}


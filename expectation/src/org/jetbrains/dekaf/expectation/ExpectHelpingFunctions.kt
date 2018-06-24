@file:JvmName("ExpectHelpingFunctions")
package org.jetbrains.dekaf.expectation


/// ARRAYS AND COLLECTIONS \\\

internal data class CheckEntry<out E>
(
        val index:   Int,
        val element: E,
        val failed:  Boolean,
        val remark:  String? = null
)

internal fun<E: Any, T:Any> MultiMatter<E,T>.checkElements(containerWord: String,
                                                           predicateDescription: String,
                                                           predicate: (E) -> Boolean): MultiMatter<E,T>
{
    val result: ArrayList<CheckEntry<E>> = ArrayList(elements.size)
    var index = 0
    var fails = 0
    for (element in elements) {
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



internal fun ByteArray.explode(): List<Byte> {
    val n = this.size
    val list = ArrayList<Byte>(n)
    for (i in 0..n-1) list += this[i]
    return list
}

internal fun ShortArray.explode(): List<Short> {
    val n = this.size
    val list = ArrayList<Short>(n)
    for (i in 0..n-1) list += this[i]
    return list
}

internal fun IntArray.explode(): List<Int> {
    val n = this.size
    val list = ArrayList<Int>(n)
    for (i in 0..n-1) list += this[i]
    return list
}

internal fun LongArray.explode(): List<Long> {
    val n = this.size
    val list = ArrayList<Long>(n)
    for (i in 0..n-1) list += this[i]
    return list
}

internal fun FloatArray.explode(): List<Float> {
    val n = this.size
    val list = ArrayList<Float>(n)
    for (i in 0..n-1) list += this[i]
    return list
}

internal fun DoubleArray.explode(): List<Double> {
    val n = this.size
    val list = ArrayList<Double>(n)
    for (i in 0..n-1) list += this[i]
    return list
}



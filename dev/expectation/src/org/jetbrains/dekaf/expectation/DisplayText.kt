package org.jetbrains.dekaf.expectation

import java.util.*


fun Any?.displayText(): String =
        if (this != null)
            when (this) {
                is CharSequence  -> this.displayText()
                is Map<*,*>      -> this.displayText()
                is Collection<*> -> this.displayText()
                is Array<*>      -> this.displayText()
                is ByteArray     -> this.displayText()
                is ShortArray    -> this.displayText()
                is IntArray      -> this.displayText()
                is LongArray     -> this.displayText()
                else             -> this.displayString()
            }
        else "null"


fun CharSequence.displayText(): String =
        if (this.isEmpty()) "empty text"
        else "text (${this.length} characters): \n\t|" +
                this.toString().replace("\n", "\n\t|")


fun<K:Any?, V:Any?> Map<K,V>.displayText(): String {
    val word = when (this) {
        is NavigableMap<*,*> -> "navigable map"
        is SortedMap<*,*> -> "sorted map"
        else -> "map"
    }

    val n = this.size
    if (n == 0) return "empty $word (type: ${this.javaClass.simpleName})"

    val b = StringBuilder()
    b.append("$word of $n entries:")
    for ((key, value) in this) {
        b.append('\n').append('\t').append(key.displayString()).append(" -> ").append(value.displayString())
    }
    return b.toString()
}

fun<E:Any?> Collection<E>.displayText(): String {
    val word = when (this) {
        is NavigableSet<*> -> "navigable set"
        is SortedSet<*>    -> "sorted set"
        is Set<*>          -> "set"
        is List<*>         -> "list"
        else               -> "collection"
    }

    val n = this.size
    if (n == 0) return "empty $word (type: ${this.javaClass.simpleName})"

    val b = StringBuilder()
    b.append("$word of $n elements:")
    for ((index, element) in this.withIndex()) {
        b.append('\n').append('\t').append('[').append(index).append(']').append('\t').append(element.displayString())
    }
    return b.toString()
}

fun<E:Any?> Array<out E>.displayText(): String {
    val n = this.size
    if (n == 0) return "empty array"
    val b = StringBuilder()
    b.append("Array of $n elements:")
    for (i in 0..n-1) b.append('\n').append('\t').append('[').append(i).append(']').append('\t').append(this[i].displayString())
    return b.toString()
}

fun ByteArray.displayText(): String {
    val n = this.size
    if (n == 0) return "empty array of bytes"
    val b = StringBuilder()
    b.append("Array of $n bytes:")
    for (i in 0..n-1) b.append('\n').append('\t').append('[').append(i).append(']').append('\t').append(this[i])
    return b.toString()
}

fun ShortArray.displayText(): String {
    val n = this.size
    if (n == 0) return "empty array of short ints"
    val b = StringBuilder()
    b.append("Array of $n short ints:")
    for (i in 0..n-1) b.append('\n').append('\t').append('[').append(i).append(']').append('\t').append(this[i])
    return b.toString()
}

fun IntArray.displayText(): String {
    val n = this.size
    if (n == 0) return "empty array of ints"
    val b = StringBuilder()
    b.append("Array of $n ints:")
    for (i in 0..n-1) b.append('\n').append('\t').append('[').append(i).append(']').append('\t').append(this[i])
    return b.toString()
}

fun LongArray.displayText(): String {
    val n = this.size
    if (n == 0) return "empty array of long ints"
    val b = StringBuilder()
    b.append("Array of $n long ints:")
    for (i in 0..n-1) b.append('\n').append('\t').append('[').append(i).append(']').append('\t').append(this[i])
    return b.toString()
}

fun FloatArray.displayText(): String {
    val n = this.size
    if (n == 0) return "empty array of float values"
    val b = StringBuilder()
    b.append("Array of $n float values:")
    for (i in 0..n-1) b.append('\n').append('\t').append('[').append(i).append(']').append('\t').append(this[i])
    return b.toString()
}

fun DoubleArray.displayText(): String {
    val n = this.size
    if (n == 0) return "empty array of double float values"
    val b = StringBuilder()
    b.append("Array of $n double float values:")
    for (i in 0..n-1) b.append('\n').append('\t').append('[').append(i).append(']').append('\t').append(this[i])
    return b.toString()
}



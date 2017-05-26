@file:JvmName("StringExt")
package org.jetbrains.dekaf.util


fun CharSequence.field(number: Int, delimiter: Char, default: String = ""): String {
    when {
        this.isEmpty() -> {
            return default
        }
        number == 1 -> {
            return this.firstField(delimiter)
        }
        number >= 2 -> {
            val d1 = this.indexOf(number-1, delimiter); if (d1 < 0) return default
            val d2 = this.indexOf(number, delimiter) ifNegative this.length
            return this.substring(d1+1,d2)
        }
        number == -1 -> {
            return this.lastField(delimiter)
        }
        number <= -2 -> {
            val revNumber = -number
            val x = this.countOf(delimiter) + 1 // count of fields
            if (x < revNumber) return default
            val m = 1 + x - revNumber
            assert(m > 0)
            return this.field(m, delimiter, default)
        }
        else -> {
            return default // however this code should not be reached
        }
    }
}

fun CharSequence.firstField(delimiter: Char): String {
    val d = this.indexOf(delimiter)
    return if (d >= 0) this.substring(0,d) else this.toString()
}

fun CharSequence.lastField(delimiter: Char): String {
    val d = this.indexOfLast { it == delimiter }
    return if (d >= 0) this.substring(d+1) else this.toString()
}

fun CharSequence.indexOf(number: Int, delimiter: Char): Int {
    if (number <= 0) return -1
    val n = this.length
    if (n == 0) return -1
    var k = 0
    for (i in 0..n-1) {
        if (this[i] == delimiter) k++
        if (k == number) return i
    }
    return -1
}

fun CharSequence.countOf(char: Char): Int {
    val n = this.length
    if (n == 0) return 0
    var count = 0
    @Suppress("LoopToCallChain")
    for (i in 0..n-1) if (this[i] == char) count++
    return count
}



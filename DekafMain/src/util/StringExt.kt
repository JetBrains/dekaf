@file:JvmName("StringExt")
package org.jetbrains.dekaf.util

import java.util.regex.Pattern


/**
 * Replaces *what* with *with*.
 *
 * As distinct from the Kotlin's function replace, it doesn't change the string
 * when the pattern is not found.
 *
 * @param what what to replace.
 * @param with what to substitute.
 */
fun String.replace(what: String, with: String): String {
    val m = what.length
    if (m == 0) throw IllegalArgumentException("The string pattern must not be empty")

    val n = this.length
    if (n < m) return this

    var p = this.indexOf(what)
    if (p < 0) return this

    val s = with.length
    val b = StringBuilder(this)
    while (p >= 0) {
        b.replace(p, p+m, with)
        p = b.indexOf(what, p+s)
    }

    return b.toString()
}


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


fun String.rtrim(): String {
    val n = length
    var k = n
    while (k > 0) {
        if (Character.isWhitespace(this[k - 1])) k--
        else break
    }

    return if (k == n) this else substring(0, k)
}


@Deprecated("use ==")
fun eq(str1: String?, str2: String?): Boolean = str1 == str2

@Deprecated("use something else")
fun eq(str1: String?, str2: String?, caseSensitive: Boolean): Boolean =
        str1 === str2 || str1 != null && str2 != null && if (caseSensitive) String.CASE_INSENSITIVE_ORDER.compare(str1,str2) == 0
                                                         else str1 == str2

fun minimizeSpaces(string: String): String {
    val s = string.trim { it <= ' ' }
    if (s.isEmpty()) return ""

    val m = SEVERAL_SPACES_PATTERN.matcher(s)
    return m.replaceAll(" ")
}

private val SEVERAL_SPACES_PATTERN = Pattern.compile("[ \\s\\t\\r\\n]{2,}")
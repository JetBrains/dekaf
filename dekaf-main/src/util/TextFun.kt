@file:JvmName("TextFun")
package org.jetbrains.dekaf.main.util


fun String.limitToOneString(limitLength: Int = 80): String {
    val n = this.length
    val b = this.indexOf('\n')
    if (n <= limitLength && b < 0) return this

    val limit = Math.min(b, limitLength)
    return if (b < 0) "${this.subSequence(0, limit)}... (a string with size $n)"
           else "${this.subSequence(0, limit)}... (a text with size $n with line breaks)"
}



fun CharSequence.indexOf(char: Char, from: Int = 0, till: Int = length, notFound: Int = -1): Int {
    for (i in from until till) if (this[i] == char) return i
    return notFound
}

fun CharSequence.indexOf(substr: String, from: Int = 0, till: Int = length, notFound: Int = -1): Int {
    val m = substr.length
    require(m > 0) { "The substring must not be empty" }
    val c1 = substr[0]
    if (m == 1) return indexOf(c1, from = from, till = till, notFound = notFound)

    val n = till - m // the last position where the first char is acceptable, inclusive
    if (n <= from) return notFound

    loop@
    for (i in from .. n) {
        if (this[i] == c1) {
            if (m == 1) return i
            for (j in 1 until m) if (this[i + j] != substr[j]) continue@loop
            return i
        }
    }

    return notFound
}


infix fun CharSequence.splitToPair(delimiter: Char): Pair<CharSequence, CharSequence> {
    val p = this.indexOf(delimiter)
    if (p >= 0) {
        // delimiter found
        val s1 = subSequence(0, p)
        val s2 = if (p + 1 < length) subSequence(p + 1, length) else ""
        return Pair(s1, s2)
    }
    else {
        // no delimiter
        return Pair(this, "")
    }
}

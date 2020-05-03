package org.jetbrains.dekaf.main.util


fun String.limitToOneString(limitLength: Int = 80): String {
    val n = this.length
    val b = this.indexOf('\n')
    if (n <= limitLength && b < 0) return this

    val limit = Math.min(b, limitLength)
    return if (b < 0) "${this.subSequence(0, limit)}... (a string with size $n)"
           else "${this.subSequence(0, limit)}... (a text with size $n with line breaks)"
}


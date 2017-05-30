package org.jetbrains.dekaf.util


fun CharSequence.abb(length: Int = 3): String {
    if (this.length <= length) return this.toString()
    val b = StringBuilder(this)
    for (i in b.length-1 downTo 1) {
        val c = b[i]
        if (c == '_' || c.isVowel()) b.deleteCharAt(i)
        if (b.length <= length) break
    }
    return b.substring(0, length)
}


fun Char.isVowel(): Boolean =
    this in "AEIOUYaeiouyАЕЁИОУЫЭЮЯаеёиоуыэюя"

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


val CharSequence?.lastCharacter: Char
    get() {
        if (this != null) {
            val n = this.length
            if (n > 0) return this[n-1]
        }

        return '\u0000'
    }


val String.capitalized: String
    get() {
        if (this.isEmpty()) return this
        val c1 = this[0]
        if (c1.isLowerCase()) {
            return c1.toUpperCase() + this.substring(1)
        }
        else {
            return this
        }
    }
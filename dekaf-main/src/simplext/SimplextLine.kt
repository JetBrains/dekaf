package org.jetbrains.dekaf.main.simplext

/**
 * Simplext line entry.
 * @param parentNode Parent line node, or the root node if it's a line without indent.
 * @param line Number of the line, starting from 1.
 * @param indent Indent width (number of spaces in the line before first character), 0 if no indent.
 * @param offset Offset of the first non-space character in the text (or file), starting from 0.
 * @param text The text (excluding indent, possible comment and line break).
 */
class SimplextLine<N> (

        val parentNode: N,
        val line: Int,
        val indent: Int,
        val offset: Int,
        val text: CharSequence

) {

    /**
     * Position in the line of the first non-space character, starting from 1.
     */
    val pos: Int
        get() = indent + 1


    override fun equals(other: Any?): Boolean {
        @Suppress("unchecked_cast")
        return other is SimplextLine<*> && equals(other as SimplextLine<N>)
    }

    fun equals(that: SimplextLine<N>): Boolean {
        return this.parentNode === that.parentNode &&
               this.line == that.line &&
               this.indent == that.indent &&
               this.offset == that.offset &&
               this.text == that.text
    }

    override fun hashCode(): Int = (line shl 16) + (pos shl 8) + offset

    override fun toString(): String = """$line,$pos[$offset]:$text"""

}

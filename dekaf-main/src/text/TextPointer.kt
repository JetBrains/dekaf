package org.jetbrains.dekaf.text

/**
 * Text Pointer - a pointer to a place in a text.
 * @author Leonid Bushuev
 */
data class TextPointer 
(
        /**
         * Offset from the beginning of the text.
         */
        val offset: Int,

        /**
         * Row number, starts with 1.
         */
        val row: Int,

        /**
         * Number of positions inside the row, starts with 1.
         */
        val pos: Int
)
{

    override fun hashCode(): Int {
        return offset
    }

    override fun toString(): String {
        return "$row:$pos($offset)"
    }

    override fun equals(other: Any?) = this === other || other is TextPointer && this.equals(other)

    // @formatter:off
    fun equals(that: TextPointer) =
            this.offset == that.offset &&
            this.row    == that.row &&
            this.pos    == that.pos
    // @formatter:on

}

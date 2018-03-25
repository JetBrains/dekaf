package org.jetbrains.dekaf.text

import java.io.Serializable


/**
 * Fragment of a text.
 */
open class TextFragment
(
        /**
         * The text of this fragment.
         */
        val text: String,

        /**
         * Row number, starts with 1.
         */
        val row: Int,

        /**
         * Number of the position inside the row, starts with 1.
         */
        val pos: Int
)
    : Comparable<TextFragment>, Serializable
{


    open val textName: String
        get() = "SQL"


    override fun compareTo(other: TextFragment): Int {
        if (this === other) return 0

        var z = this.textName.compareTo(other.textName)
        if (z == 0) z = this.row - other.row
        if (z == 0) z = this.pos - other.pos
        if (z == 0) z = this.text.compareTo(other.text)
        return z
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as TextFragment

        return this.row == that.row
            && this.pos == that.pos
            && this.text == that.text
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }


    protected open val textLocationHumanReadable: String
        get() = "Fragment at $row:$pos"


    override fun toString(): String {
        return textLocationHumanReadable + '\n' + text
    }

}

package org.jetbrains.dekaf.text

import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Text with a position pointer. Also counts line numbers.
 * @author Leonid Bushuev
 */
class TextWalker(val text: String) : Cloneable {

    /**
     * The length of the text.
     */
    val length: Int = text.length

    /**
     * Current offset from the beginning of the text.
     */
    var offset = 0
        private set

    /**
     * Current row number, starts with 1.
     */
    var row = 1
        private set

    /**
     * Current number of positions inside the row, starts with 1.
     */
    var pos = 1
        private set

    /**
     * Current character.
     */
    var char: Char = if (text.isNotEmpty()) text[0] else '\u0000'
        private set

    /**
     * Current text pointer.
     */
    var pointer: TextPointer
        get() = TextPointer(offset, row, pos)
        set(pointer) {
            offset = pointer.offset
            row = pointer.row
            pos = pointer.pos
            char = if (length > 0) text[0] else '\u0000'
        }


    operator fun next() {
        if (offset >= length) throw IllegalStateException("End of Text")

        if (char == '\n') {
            row++
            pos = 1
        }
        else {
            pos++
        }

        offset++
        char = if (offset < length) text[offset] else '\u0000'
    }


    fun popRow(): String {
        val offset1 = offset
        val row1 = row
        while (row1 == row && !isEOT) next()
        return text.substring(offset1, offset)
    }


    fun skipToPattern(pattern: Pattern): Matcher? {
        val m = pattern.matcher(text)
        val found = m.find(offset)
        if (found) {
            val goal = m.start()
            while (offset < goal) next()
            return m
        }
        else {
            while (offset < length) next()
            return null
        }
    }


    fun skipToOffset(newOffset: Int) {
        if (newOffset < offset) throw IllegalArgumentException("Attempted to skip back")
        if (newOffset > length) throw IllegalArgumentException("Attempted to skip out of the end of text")

        val distance = newOffset - offset
        for (i in 1..distance) next()
    }

    val nextChar: Char
        get() {
            val k = offset + 1
            return if (k < length) text[k] else '\u0000'
        }


    val isEOT: Boolean
        get() = offset >= length


    public override fun clone(): TextWalker {
        val clone = TextWalker(text)
        clone.offset = offset
        clone.row = row
        clone.pos = pos
        clone.char = char
        return clone
    }


}

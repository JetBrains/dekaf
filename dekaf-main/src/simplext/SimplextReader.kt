package org.jetbrains.dekaf.main.simplext

import org.jetbrains.dekaf.main.util.indexOf
import java.lang.Math.min
import java.util.*

/**
 *
 */
abstract class SimplextReader<N>(

        /**
         * Root node.
         */
        val rootNode: N,

        /**
         * Line handler.
         */
        val handler: (SimplextLine<N>) -> N

) {

    private class Level<N> (val line: SimplextLine<N>, val node: N)

    /**
     * Tabulation width (default is 8).
     */
    var tabWidth: Int = 8

    /**
     * Line comment marker.
     */
    var lineCommentMark: String? = null

    /**
     * Offset of the buffer in the whole text or file.
     */
    protected var bufferOffset = 0

    /**
     * Structure stack
     */
    private val stack = Stack<Level<N>>()

    private var lastLine: Int = 0


    /**
     * Process a line of text.
     *
     * @param buffer text buffer (offset 0 in this buffer represents offset [bufferOffset] in the file).
     * @param from   line starts at this offset in the buffer (inclusive)
     * @param till   line ends at this offset in the buffer (exclusive),
     *               i.e. the position [till] holds a line break or is staying behind the last character of the buffer.
     */
    protected fun processLine(buffer: CharSequence, from: Int, till: Int) {
        lastLine++
        val n = min(buffer.length, till)
        var p = from
        if (p >= n) return

        var ind = 0
        var c = buffer[p]
        while (p < n && c.isWhitespace()) {
            if (c == '\t') ind = handleTab(ind)
            else ind++
            p++
            if (p >= n) return
            c = buffer[p]
        }

        var q = n
        val lcm = lineCommentMark
        if (lcm != null) q = buffer.indexOf(lcm, p, n, n)
        if (p == q) return // the line contains a comment only

        while (p < q && buffer[q - 1].isWhitespace()) q--
        if (p == q) return // this should never happened

        val lineText = buffer.subSequence(p, q)

        val indent = p - from
        unrollStackForIndent(indent)

        val entry = SimplextLine(getParentNode(), lastLine, indent, bufferOffset + p, lineText)
        val entryNode = handler(entry)
        val level = Level(entry, entryNode)
        stack.push(level)
    }

    private fun unrollStackForIndent(indent: Int) {
        if (indent == 0) {
            stack.clear()
        }
        else {
            while (stack.isNotEmpty() && stack.peek().line.indent >= indent) stack.pop()
        }
    }

    private fun getParentNode(): N = if (stack.isNotEmpty()) stack.peek().node else rootNode

    private fun handleTab(ind: Int): Int {
        return (ind / tabWidth + 1) * tabWidth
    }

}
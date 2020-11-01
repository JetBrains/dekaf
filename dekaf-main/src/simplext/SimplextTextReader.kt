package org.jetbrains.dekaf.main.simplext

import org.jetbrains.dekaf.main.util.indexOf

/**
 *
 */
class SimplextTextReader<N> : SimplextReader<N> {

    constructor(rootNode: N, handler: (SimplextLine<N>) -> N) : super(rootNode, handler)


    fun processText(text: CharSequence) {
        val n = text.length
        var p = 0
        while (p < n) {
            val q = text.indexOf('\n', from = p, notFound = n)
            processLine(text, from = p, till = q)
            p = q + 1
        }
    }

}
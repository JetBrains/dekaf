package org.jetbrains.dekaf.sqlm.parser

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.Reader

/**
 *
 */
internal object SimpleParser
{

    internal fun parse(reader: Reader): SQLMParser.FileContext {
        @Suppress("DEPRECATION")
        val input = ANTLRInputStream(reader)

        val lexer = SQLMLexer(input)
        val tokens = CommonTokenStream(lexer)
        val parser = SQLMParser(tokens)

        val tree: SQLMParser.FileContext = parser.file()

        return tree
    }

}
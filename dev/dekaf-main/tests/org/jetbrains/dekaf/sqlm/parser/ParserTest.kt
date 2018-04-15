package org.jetbrains.dekaf.sqlm.parser

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringReader

/**
 * Test on the SQLM grammar.
 */
@Tag("basic")
class ParserTest
{

    companion object
    {
        const val basePath = "org/jetbrains/dekaf/sqlm/parser/"

        @JvmField
        val exampleNames = arrayOf(
                "one_statement.sqlm",
                "one_statement_with_header.sqlm"
        )

        @JvmStatic
        fun obtainExampleNames() = exampleNames
    }


    @ParameterizedTest
    @MethodSource("obtainExampleNames")
    fun example(name: String) {
        val path = basePath + name
        val sourceText = readResourceText(path)
        val expectedText = readResourceText(path + ".txt").trim()

        val sourceReader = StringReader(sourceText)

        val tree = SimpleParser.parse(sourceReader)

        val treeRepresentation = tree.toStringTree(SQLMParser.ruleNames.asList())
        
        Assertions.assertEquals(expectedText, treeRepresentation)
    }

    
    private fun readResourceText(path: String): String {
        val b = StringBuilder()
        val stream = this.javaClass.classLoader.getResourceAsStream(path)
                ?: throw IllegalArgumentException("No resource file: " + path)
        val reader1 = InputStreamReader(stream, Charsets.UTF_8)
        val reader2 = BufferedReader(reader1)
        while (true) {
            val line = reader2.readLine() ?: break
            b.append(line).append(System.lineSeparator())
        }
        stream.close()
        return b.toString()
    }


}
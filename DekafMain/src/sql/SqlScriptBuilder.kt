package org.jetbrains.dekaf.sql

import org.jetbrains.dekaf.text.TextPointer
import org.jetbrains.dekaf.text.TextWalker
import org.jetbrains.dekaf.util.*

import java.util.ArrayList
import java.util.Collections
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * A builder for SQL script.
 * 
 * @author Leonid Bushuev
 */
class SqlScriptBuilder {

    private val myStatements = ArrayList<SqlStatement>()


    fun add(vararg commands: String) {
        for (command in commands) {
            val cmd = SqlCommand(command)
            myStatements.add(cmd)
        }
    }

    fun add(vararg commands: SqlCommand) {
        Collections.addAll(myStatements, *commands)
    }

    fun add(vararg scripts: SqlScript) {
        for (script in scripts) {
            if (script.hasStatements()) {
                for (statement in script.statements) {
                    myStatements.add(statement)
                }
            }
        }
    }


    fun parse(text: String) {
        val walker = TextWalker(text)
        while (!walker.isEOT) {
            skipEmptySpace(walker)
            if (walker.isEOT) break

            val essentialWords = extractEssentialWords(walker, 6)
            val pl = determinePL(essentialWords)
            if (pl) {
                extractPLBlock(walker)
            }
            else {
                extractSQLCommand(walker)
            }
        }
    }


    private fun extractPLBlock(walker: TextWalker) {
        val (offset, row1) = walker.pointer
        var position: Int
        while (true) {
            position = walker.offset
            if (walker.isEOT) break
            val row = walker.popRow().trim { it <= ' ' }
            if (row == "/") break
        }

        val plText = walker.text.substring(offset, position).rtrim()
        val command = SqlCommand(plText, row1, null)
        myStatements.add(command)
    }

    private fun extractSQLCommand(walker: TextWalker) {
        skipEmptySpace(walker)
        val (offset, row) = walker.pointer
        val matcher = walker.skipToPattern(SQL_END_MARKER)

        val sqlText = walker.text
                .substring(offset, walker.offset).rtrim()
        val command = SqlCommand(sqlText, row, null)
        myStatements.add(command)

        if (matcher != null) {
            walker.skipToOffset(matcher.end())
        }
    }


    internal fun determinePL(essentialWords: String): Boolean {
        return PL_ESSENTIAL_WORDS_PATTERN.matcher(essentialWords).matches()
    }


    protected fun skipEmptySpace(walker: TextWalker) {
        while (!walker.isEOT) {
            val c1 = walker.char
            if (Character.isWhitespace(c1)) {
                walker.next()
                continue
            }
            val c2 = walker.nextChar
            if (c1 == '-' && c2 == '-') {
                while (!walker.isEOT) {
                    walker.next()
                    if (walker.char == '\n') {
                        walker.next()
                        break
                    }
                }
            }
            if (c1 == '/' && c2 == '*') {
                walker.next()
                walker.next()
                while (!walker.isEOT) {
                    if (walker.char == '*' && walker.nextChar == '/') {
                        walker.next()
                        walker.next()
                        break
                    }
                    walker.next()
                }
            }
            break
        }
    }


    fun build(): SqlScript {
        return SqlScript(myStatements)
    }

    companion object {


        internal fun extractEssentialWords(walker: TextWalker, limitWords: Int): String {
            val w = walker.clone()
            val b = StringBuilder(40)
            var wordsCnt = 0
            var inWord = false
            var inSingleLineComment = false
            var inMultiLineComment = false
            while (!w.isEOT) {
                val c = w.char
                val c2 = w.nextChar
                val isWordChar = Character.isJavaIdentifierPart(c)
                if (inSingleLineComment) {
                    if (c == '\n') inSingleLineComment = false
                }
                else if (inMultiLineComment) {
                    if (c == '*' && c2 == '/') {
                        w.next() // additional w.next() - because 2 chars
                        inMultiLineComment = false
                    }
                }
                else if (inWord && !isWordChar) {
                    wordsCnt++
                    if (wordsCnt >= limitWords) break
                    b.append(' ')
                    inWord = false
                }
                else if (isWordChar) {
                    b.append(Character.toLowerCase(c))
                    inWord = true
                }
                else if (!isWordChar) {
                    if (c == '-' && c2 == '-') {
                        inSingleLineComment = true
                        w.next()
                    }
                    if (c == '/' && c2 == '*') {
                        inMultiLineComment = true
                        w.next()
                    }
                }
                w.next()
            }
            return b.toString().trim { it <= ' ' }
        }


        private val SQL_END_MARKER = Pattern.compile(
                """(;|\n\s*/)(\s|\n|--[^\n]*?\n|/\*.*?\*/)*?(\n|$)|$""",
                Pattern.DOTALL)


        private val PL_ESSENTIAL_WORDS_PATTERN = Pattern.compile(
                """^(declare|begin|(create (or replace )?(type|package|procedure|function|trigger))).*""")
    }


}

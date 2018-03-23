package org.jetbrains.dekaf.text

import org.jetbrains.dekaf.util.lastField
import org.jetbrains.dekaf.util.minimizeSpaces
import org.jetbrains.dekaf.util.replace
import org.jetbrains.dekaf.util.rtrim
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * @author Leonid Bushuev
 */
class ScriptumResourceFromJava : ScriptumResource {

    private val myClassLoader: ClassLoader

    private val myResourcePath: String

    private val myFileName: String


    constructor(classLoader: ClassLoader, resourcePath: String) {
        myClassLoader = classLoader
        myResourcePath = resourcePath
        myFileName = resourcePath.lastField('/')
    }


    override fun loadFragments(): Array<TextFileFragment> {
        try {
            val stream = openStream()
            try {
                val reader1 = InputStreamReader(stream, Charsets.UTF_8)
                try {
                    val reader2 = BufferedReader(reader1)
                    try {
                        return loadFragmentsFromReader(reader2)
                    }
                    finally {
                        reader2.close()
                    }
                }
                finally {
                    reader1.close()
                }
            }
            finally {
                stream.close()
            }
        }
        catch (ioe: IOException) {
            throw RuntimeException(String.format("cannot load resource  %s: %s", myResourcePath, ioe.message))
        }

    }

    private fun openStream(): InputStream {
        return myClassLoader.getResourceAsStream(myResourcePath)
    }


    @Throws(IOException::class)
    private fun loadFragmentsFromReader(reader: BufferedReader): Array<TextFileFragment> {
        val fragments = ArrayList<TextFileFragment>()
        val buf = StringBuilder()
        var currentName = "0"
        var row = 0
        var fragmentRow = 1
        while (true) {
            var line: String? = reader.readLine()
            row++

            if (line == null) break
            line = line.rtrim()
            val m = SECTION_HEADER_PATTERN.matcher(line)
            if (m.matches()) {
                // first save the previous text
                putTheText(fragments, buf, fragmentRow, currentName)
                // now start the new text
                fragmentRow = row + 1
                buf.delete(0, buf.length)
                currentName = normalizeName(m)
            }
            else if (line.isEmpty() && buf.isEmpty()) {
                fragmentRow++ // skipping empty lines before command text
            }
            else {
                buf.append(line).append('\n')
            }
        }
        putTheText(fragments, buf, fragmentRow, currentName)
        return fragments.toTypedArray()
    }

    private fun normalizeName(m: Matcher): String {
        return minimizeSpaces(m.group(1)).replace(" + ", "+")
    }

    private fun putTheText(fragments: ArrayList<TextFileFragment>,
                           buf: StringBuilder,
                           row: Int,
                           name: String) {
        val text = buf.toString().rtrim()
        val fragment = TextFileFragment(text, myFileName, row, 1, name)
        fragments.add(fragment)
    }

    companion object {

        @JvmStatic
        private val SECTION_HEADER_PATTERN = Pattern.compile(
                """^\s*-{4,}\s*(([\p{L}\p{javaJavaIdentifierPart}$-]+)(\s*\+\s*([\p{L}\p{javaJavaIdentifierPart}$-]+))?)\s*-{4,}\s*$""",
                Pattern.UNICODE_CASE or Pattern.CASE_INSENSITIVE)

    }

}

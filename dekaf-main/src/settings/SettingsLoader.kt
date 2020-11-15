package org.jetbrains.dekaf.main.settings

import org.jetbrains.dekaf.inter.settings.Settings
import org.jetbrains.dekaf.main.simplext.SimplextFileReader
import org.jetbrains.dekaf.main.simplext.SimplextLine
import org.jetbrains.dekaf.main.simplext.SimplextReader
import org.jetbrains.dekaf.main.simplext.SimplextTextReader
import org.jetbrains.dekaf.main.util.splitToPair
import java.io.Serializable
import java.nio.file.Path

/**
 *
 */
class SettingsLoader {

    var tabWidth = 8

    var fileName: String? = null

    var errorHandler: (Error) -> Unit = ::defaultErrorHandler



    class Error (val line: Int, val message: String, val exception: Exception?)


    fun load(text: CharSequence): Settings {
        val builder = SettingsBuilder()
        val simReader = SimplextTextReader<SettingsBuilder?>(builder, ::handleLine)
        prepareSimplextReader(simReader)
        simReader.processText(text)
        return builder.build()
    }

    fun load(file: Path): Settings {
        val builder = SettingsBuilder()
        val simReader = SimplextFileReader<SettingsBuilder?>(builder, ::handleLine)
        prepareSimplextReader(simReader)
        simReader.processFile(file)
        return builder.build()
    }

    private fun prepareSimplextReader(simReader: SimplextReader<SettingsBuilder?>) {
        simReader.tabWidth = tabWidth
        simReader.lineCommentMark = "//"
    }


    private fun handleLine(sl: SimplextLine<SettingsBuilder?>): SettingsBuilder? {
        val parentBuilder = sl.parentNode
        if (parentBuilder == null) {
            reportError(sl, "No context to apply setting (parent setting should be a context header)")
            return null
        }

        try {
            val text = sl.text
            val n = text.length
            if (n >= 2 && text.endsWith(':')) {
                val pathStr = text.subSequence(0, n-1)
                val path = decodeNamePath(pathStr)
                val value = parentBuilder[path]
                if (value == null) {
                    val innerBuilder = SettingsBuilder()
                    parentBuilder[path] = innerBuilder
                    return innerBuilder
                }
                else if (value is SettingsBuilder) {
                    return value
                }
                else {
                    val pathStr2 = path.joinToString(separator = ".")
                    reportError(sl, """Context collision: the setting "$pathStr2" already assigned.""")
                    return null
                }
            }
            else {
                val (nameStr, valueStr) = text.splitToPair('=')
                val path = decodeNamePath(nameStr)
                val value = decodeValue(valueStr)
                parentBuilder[path] = value
                return null
            }
        }
        catch (e: Exception) {
            val problemMessage = e.message
            reportError(sl, problemMessage ?: "Exception " + e.javaClass.simpleName, e)
            return null
        }
    }


    private fun reportError(simplextLine: SimplextLine<SettingsBuilder?>, message: String, exception: Exception? = null) {
        val line = simplextLine.line
        val where = if (fileName != null) "at $fileName:$line" else "in line $line"
        val fullMessage = "Error $where: $message"
        errorHandler(Error(line, fullMessage, exception))
    }



    private companion object {

        @JvmStatic
        private fun decodeNamePath(str: CharSequence): Array<String> {
            val parts = str.split('.')
            val n = parts.size
            return Array<String>(n) { i -> parts[i].trim() }
        }

        @JvmStatic
        private fun decodeValue(valueStr: CharSequence): Serializable? {
            val str = valueStr.trim()
            val n = str.length
            if (n == 0) return null

            val c1 = str[0]
            if (n == 1) return decodeShortValue(c1)
            if (c1 == '\'' || c1 == '"') return unquote(str)
            // TODO booleans
            // TODO numbers
            return str.toString()
        }

        @JvmStatic
        private fun decodeShortValue(char: Char): Serializable? {
            if (char.isWhitespace()) return null
            return when (char) {
                in '0' .. '9' -> (char.toShort() - 48.toShort()).toByte()
                else -> char
            }
        }

        @JvmStatic @Throws(QuotationException::class)
        private fun unquote(str: CharSequence): String {
            val n = str.length
            val q = str[0]
            if (str[n-1] != q) throw QuotationException("Cannot unquote string because no closing quote. The string: $str")

            val b = StringBuilder(n)
            var i = 1
            while (i < n - 1) {
                val c = str[i++]
                when (c) {
                    '\\' -> {
                        val x = str[i++]
                        val cx =
                                when (x) {
                                    't' -> '\t'
                                    'n' -> '\n'
                                    'r' -> '\r'
                                    '\\' -> '\\'
                                    '\'' -> '\''
                                    '"' -> '"'
                                    else -> throw QuotationException("Cannot unescape symbol \\$x. The string: $str")
                                }
                        b.append(cx)
                    }
                    q -> {
                        throw QuotationException("Cannot unquote string because an unescaped quote in the middle. The string: $str")
                    }
                    else -> {
                        b.append(c)
                    }
                }
            }

            return b.toString()
        }

        @JvmStatic
        private fun defaultErrorHandler(error: Error): Nothing =
                throw LoadingException("Failed to load settings: " + error.message, error.exception)


    }


    class QuotationException(message: String) : Exception(message)

    class LoadingException(message: String, cause: Exception?) : Exception(message, cause)

}
package org.jetbrains.dekaf.text

import org.jetbrains.dekaf.core.QueryResultLayout
import org.jetbrains.dekaf.sql.SqlCommand
import org.jetbrains.dekaf.sql.SqlQuery
import org.jetbrains.dekaf.sql.SqlScript
import org.jetbrains.dekaf.sql.SqlScriptBuilder
import org.jetbrains.dekaf.util.*

import java.util.ArrayList
import java.util.regex.Pattern



/**
 * Scriptum provides with access to SQL texts that are placed in separate files
 * (not within Java code).

 * @author Leonid Bushuev from JetBrains
 */
class Scriptum
private constructor
(
        private val myResources: Array<ScriptumResource>,

        private val myDialect: String?
)
{

    ////// INITIALIZATION \\\\\\

    companion object {

        fun of(clazz: Class<*>): Scriptum {
            val classLoader = clazz.classLoader
            val className = clazz.name
            val path = className.replace('.', '/')
            val resourceName = path + ".sql"

            return of(classLoader, resourceName)
        }

        fun of(clazz: Class<*>, name: String): Scriptum {
            val classLoader = clazz.classLoader
            val packageName = clazz.`package`.name
            val path = packageName.replace('.', '/')
            var fileName = name
            if (!fileName.contains(".")) fileName += ".sql"
            val resourceName = path + '/' + fileName

            return of(classLoader, resourceName)
        }

        fun dialectOf(clazz: Class<*>, dialect: String?): Scriptum {
            val classLoader = clazz.classLoader
            val className = clazz.name
            val path = className.replace('.', '/')
            val sr = ArrayList<ScriptumResource>(2)

            if (dialect != null) {
                val name1 = "$path+$dialect.sql"
                val exist1 = classLoader.getResource(name1) != null
                if (exist1) {
                    val r1 = ScriptumResourceFromJava(classLoader, name1)
                    sr.add(r1)
                }
            }

            val name2 = path + ".sql"
            val exist2 = classLoader.getResource(name2) != null
            if (exist2) {
                val r2 = ScriptumResourceFromJava(classLoader, name2)
                sr.add(r2)
            }

            if (sr.isEmpty())
                throw IllegalArgumentException("Resources for class $className not found")

            val resources = sr.toTypedArray()
            return Scriptum(resources, dialect)
        }

        fun dialectOf(parentScriptum: Scriptum, dialect: String?): Scriptum {
            if (parentScriptum.myDialect == dialect) {
                return parentScriptum
            }
            else {
                return Scriptum(parentScriptum.myResources, dialect)
            }
        }


        fun of(classLoader: ClassLoader, resourceName: String): Scriptum {
            val sr = ArrayList<ScriptumResource>(2)
            val exists = classLoader.getResource(resourceName) != null
            if (exists) {
                val r2 = ScriptumResourceFromJava(classLoader, resourceName)
                sr.add(r2)
            }
            else {
                throw IllegalArgumentException(String.format("Resources for class %s not found",
                                                             resourceName))
            }

            val resources = sr.toTypedArray()
            return Scriptum(resources, null)
        }

        private val STRIP_SINGLE_STATEMENT_PATTERN =
                Pattern.compile("""((;\s*)+|(\n/\s*\n?)+)$""",
                                Pattern.DOTALL)
    }



    ////// FUNCTIONS TO QUERY TEXTS \\\\\\

    /**
     * Finds the requested script.
     *
     * If no such script, returns null.
     *
     * @param name  name of the script to find.
     * @return      the found script, or *null* if not found.
     */
    fun findText(name: String): TextFileFragment? {
        var fragment: TextFileFragment?
        val nameWithDialect = if (myDialect == null) null else name + '+' + myDialect
        for (i in myResources.indices.reversed()) {
            val r = myResources[i]
            if (nameWithDialect != null) {
                fragment = r.find(nameWithDialect)
                if (fragment != null) return fragment
            }
            fragment = r.find(name)
            if (fragment != null) return fragment
        }
        return null
    }

    /**
     * Provides with the requested script.
     *
     * If no such script, throws [ScriptNotFoundException].
     *
     * @param name  name of the script to provide with.
     * @return      the script.
     * @throws ScriptNotFoundException if no such script.
     */
    @Throws(ScriptNotFoundException::class)
    fun getText(name: String): TextFileFragment {
        val fragment = findText(name)
        if (fragment != null) {
            return fragment
        }
        else {
            // some useful diagnostics
            val b = StringBuilder()
            b.append("No such script with name: ").append(name).append('\n')
            var was = false
            for (r in myResources) {
                for (existentName in r.existentNames) {
                    if (!was) {
                        b.append("There are scripts: ")
                        was = true
                    }
                    else {
                        b.append(", ")
                    }
                    b.append(existentName)
                }
            }
            if (!was) {
                b.append("There are no scripts at all")
            }
            throw ScriptNotFoundException(b.toString())
        }
    }


    fun <S> query(name: String,
                  layout: QueryResultLayout<S>): SqlQuery<S> {
        var fragment = getText(name)
        fragment = stripSingleStatement(fragment)
        val query = SqlQuery(fragment, layout)
        query.displayName = fragment.fragmentName
        return query
    }

    fun command(name: String): SqlCommand {
        var fragment = getText(name)
        fragment = stripSingleStatement(fragment)
        return SqlCommand(fragment)
    }

    fun script(name: String): SqlScript {
        val fragment = getText(name)
        val b = SqlScriptBuilder()
        b.parse(fragment.text)
        return SqlScript(b.build())
    }

    private fun stripSingleStatement(fragment: TextFileFragment): TextFileFragment {
        val m = STRIP_SINGLE_STATEMENT_PATTERN.matcher(fragment.text)
        if (m.find()) {
            var n = fragment.text.length
            n -= m.group(1).length
            val text = fragment.text.substring(0, n).rtrim()
            return TextFileFragment(text,
                                    fragment.textName,
                                    fragment.row, fragment.pos,
                                    fragment.fragmentName)
        }
        else {
            return fragment
        }
    }


    ////// EXCEPTIONS \\\\\\

    class ScriptNotFoundException internal constructor(message: String) : RuntimeException(message)

}

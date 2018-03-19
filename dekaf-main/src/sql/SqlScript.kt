package org.jetbrains.dekaf.sql

import java.util.*


/**
 * One SQL script.
 *
 * @author Leonid Bushuev
 */
class SqlScript {

    private val statements_: Array<out SqlStatement>

    private val count_: Int

    val statements: List<SqlStatement>
        get() = Collections.unmodifiableList(Arrays.asList(*statements_))


    constructor(vararg statements: String)
            : this(Array<SqlStatement>(statements.size) { i -> SqlCommand(statements[i]) }, false)

    constructor(vararg statements: SqlStatement)
            : this(statements, true)

    constructor(vararg scripts: SqlScript)
            : this(joinStatements(scripts), false)

    constructor(statements: Collection<SqlStatement>)
            : this(statements.toTypedArray(), false)

    private constructor(statements: Array<out SqlStatement>, copy: Boolean) {
        val n = statements.size
        this.statements_ =
                if (copy && n > 0) Array(n) { i -> statements[i] }
                else statements
        this.count_ = n
    }


    fun hasStatements(): Boolean {
        return count_ > 0
    }

    fun count(): Int {
        return count_
    }

    override fun toString(): String {
        when (count_) {
            0    -> return ""
            1    -> return statements_[0].sourceText
            else -> {
                val b = StringBuilder()
                val delimiterString = scriptDelimiterString
                b.append(statements_[0].sourceText)
                for (i in 1..count_ - 1) {
                    if (b[b.length - 1] != '\n') b.append('\n')
                    b.append(delimiterString).append('\n')
                    b.append(statements_[i].sourceText)
                }
                return b.toString()
            }
        }
        // TODO cache it
    }


    protected val scriptDelimiterString: String
        get() = ";"


    companion object {

        @JvmStatic
        private fun joinStatements(scripts: Array<out SqlScript>): Array<SqlStatement> {
            val b = ArrayList<SqlStatement>()
            for (script in scripts) {
                b.addAll(script.statements)
            }
            return b.toTypedArray()
        }

    }
}

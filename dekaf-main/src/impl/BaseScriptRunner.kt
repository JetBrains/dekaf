package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.core.DBScriptRunner
import org.jetbrains.dekaf.sql.SqlCommand
import org.jetbrains.dekaf.sql.SqlQuery
import org.jetbrains.dekaf.sql.SqlScript

/**
 * Script runner.
 */
internal class BaseScriptRunner : BaseRunner, DBScriptRunner {

    private val script: SqlScript


    constructor(session: BaseSession, script: SqlScript) : super(session) {
        this.script = script
    }


    override fun run(): BaseScriptRunner {
        val n = script.count()

        // make runners
        val runners = Array<BaseStatementRunner>(n) { index ->
            val statement = script.statements[index]
            when (statement) {
                is SqlQuery<*> -> session.query(statement)
                is SqlCommand  -> session.command(statement)
                else           -> throw IllegalArgumentException("Unknown how to deal with class "+statement.javaClass.name)
            }
        }

        // run
        for (runner in runners) {
            try {
                runner.prepare()
                runner.run()
            }
            finally {
                runner.close()
            }
        }

        // ok
        return this
    }


    override fun close() {}

}
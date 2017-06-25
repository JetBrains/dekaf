package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.sql.SqlCommand
import org.jetbrains.dekaf.sql.SqlQuery
import org.jetbrains.dekaf.sql.SqlScript


/**
 * Database transaction.
 */
interface DBTransaction {

    fun command(command: SqlCommand): DBCommandRunner

    fun command(commandText: String): DBCommandRunner

    fun <S> query(query: SqlQuery<S>): DBQueryRunner<S>

    fun <T> query(queryText: String, layout: QueryResultLayout<T>): DBQueryRunner<T>

    fun script(script: SqlScript): DBScriptRunner

}

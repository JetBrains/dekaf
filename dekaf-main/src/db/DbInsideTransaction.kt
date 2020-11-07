package org.jetbrains.dekaf.main.db

import org.jetbrains.dekaf.main.queries.Query
import org.jetbrains.dekaf.main.queries.QueryLayout


interface DbInsideTransaction {

    /**
     * Performs a simple statement without parameters or result sets.
     */
    fun perform(statementText: String)


    fun<T> query(queryText: String, layout: QueryLayout<T>): DbQueryRunner<T>

    fun<T> query(query: Query<T>): DbQueryRunner<T>

}
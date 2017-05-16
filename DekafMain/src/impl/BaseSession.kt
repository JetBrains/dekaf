package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.core.*
import org.jetbrains.dekaf.inter.InterSession
import org.jetbrains.dekaf.sql.SqlCommand
import org.jetbrains.dekaf.sql.SqlQuery
import org.jetbrains.dekaf.sql.SqlScript


internal open class BaseSession: DBLeasedSession {

    /// SETTINGS \\\

    val facade: BaseFacade

    val inter: InterSession


    /// STATE \\\

    var closed: Boolean = false

    var insideTran: Boolean = false


    /// INITIALIZATION \\\

    constructor(facade: BaseFacade, inter: InterSession) {
        this.facade = facade
        this.inter = inter
    }


    /// TRANSACTIONS \\\

    override fun <R> inTransaction(operation: InTransaction<R>): R {
        beginTransaction()
        try {
            val result = operation.run(this)
            commit()
            return result
        }
        catch (e: Exception) {
            rollback()
            throw e
        }
    }

    override fun inTransaction(operation: InTransactionNoResult) {
        beginTransaction()
        try {
            operation.run(this)
            commit()
        }
        catch (e: Exception) {
            rollback()
            throw e
        }
    }

    override fun beginTransaction() {
        TODO("not implemented yet")
    }

    override val isInTransaction: Boolean
        get() = insideTran

    override fun commit() {
        TODO("not implemented yet")
    }

    override fun rollback() {
        TODO("not implemented yet")
    }


    /// RUNNERS \\\

    override fun command(command: SqlCommand): DBCommandRunner {
        TODO("not implemented yet")
    }

    override fun command(commandText: String): DBCommandRunner {
        TODO("not implemented yet")
    }

    override fun <S> query(query: SqlQuery<S>): DBQueryRunner<S> {
        TODO("not implemented yet")
    }

    override fun <S> query(queryText: String, layout: ResultLayout<S>): DBQueryRunner<S> {
        TODO("not implemented yet")
    }

    override fun script(script: SqlScript): DBScriptRunner {
        TODO("not implemented yet")
    }

    override fun ping(): Long {
        return inter.ping().toLong()
    }

    override val isClosed: Boolean
        get() = closed

    override fun close() {
        inter.close()
        closed = true
        facade.sessionClosed(this)
    }

    override fun <I : Any?> getSpecificService(serviceClass: Class<I>, serviceName: String): I? {
        TODO("not implemented yet")
    }

}
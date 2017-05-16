package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.core.*
import org.jetbrains.dekaf.inter.InterSession
import org.jetbrains.dekaf.sql.SqlCommand
import org.jetbrains.dekaf.sql.SqlQuery
import org.jetbrains.dekaf.sql.SqlScript


internal open class BaseSession: DBLeasedSession {

    val facade: BaseFacade

    val inter: InterSession


    constructor(facade: BaseFacade, inter: InterSession) {
        this.facade = facade
        this.inter = inter
    }


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

    override fun <R> inTransaction(operation: InTransaction<R>): R {
        TODO("not implemented yet")
    }

    override fun beginTransaction() {
        TODO("not implemented yet")
    }

    override val isClosed: Boolean
        get() = TODO("not implemented yet")

    override fun inTransaction(operation: InTransactionNoResult) {
        TODO("not implemented yet")
    }

    override fun close() {
        inter.close()
        facade.sessionClosed(this)
    }

    override fun <I : Any?> getSpecificService(serviceClass: Class<I>, serviceName: String): I? {
        TODO("not implemented yet")
    }

    override val isInTransaction: Boolean
        get() = TODO("not implemented yet")

    override fun commit() {
        TODO("not implemented yet")
    }

    override fun rollback() {
        TODO("not implemented yet")
    }
}
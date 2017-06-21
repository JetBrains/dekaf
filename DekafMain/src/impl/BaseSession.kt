package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.core.*
import org.jetbrains.dekaf.inter.InterSession
import org.jetbrains.dekaf.sql.SqlCommand
import org.jetbrains.dekaf.sql.SqlQuery
import org.jetbrains.dekaf.sql.SqlScript
import java.util.function.Function
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.function.Consumer


internal open class BaseSession: DBLeasedSession {

    /// SETTINGS \\\

    val facade: BaseFacade

    val inter: InterSession


    /// STATE \\\

    var closed: Boolean = false

    var insideTran: Boolean = false

    val runners = ConcurrentLinkedDeque<BaseRunner>()


    /// INITIALIZATION \\\

    constructor(facade: BaseFacade, inter: InterSession) {
        this.facade = facade
        this.inter = inter
    }


    /// TRANSACTIONS \\\

    override fun <R : Any?> inTransaction(operation: Function<DBTransaction, R>): R {
        beginTransaction()
        try {
            val result = operation.apply(this)
            commit()
            return result
        }
        catch (e: Exception) {
            rollback()
            throw e
        }
    }

    override fun inTransactionDo(operation: Consumer<DBTransaction>) {
        beginTransaction()
        try {
            operation.accept(this)
            commit()
        }
        catch (e: Exception) {
            rollback()
            throw e
        }
    }

    override fun beginTransaction() {
        inter.begin()
        insideTran = true
    }

    override val isInTransaction: Boolean
        get() = insideTran

    override fun commit() {
        inter.commit()
        insideTran = false
    }

    override fun rollback() {
        inter.rollback()
        insideTran = false
    }


    /// RUNNERS \\\

    override fun command(command: SqlCommand): DBCommandRunner {
        val commandText = command.sourceText
        return command(commandText)
    }

    override fun command(commandText: String): DBCommandRunner {
        val interSeance = inter.openSeance()
        val runner = BaseCommandRunner(this, interSeance, commandText)
        runners.add(runner)
        runner.prepare()
        return runner
    }

    override fun <S> query(query: SqlQuery<S>): DBQueryRunner<S> {
        TODO("not implemented yet")
    }

    override fun <T> query(queryText: String, layout: QueryResultLayout<T>): DBQueryRunner<T> {
        val interSeance = inter.openSeance()
        val runner = BaseQueryRunner(this, interSeance, queryText, layout)
        runners.add(runner)
        runner.prepare()
        return runner
    }

    override fun <S> query(queryText: String, layout: ResultLayout<S>): DBQueryRunner<S> {
        TODO("not implemented yet")
    }

    override fun script(script: SqlScript): DBScriptRunner {
        TODO("not implemented yet")
    }

    override fun ping(): Int {
        return inter.ping()
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


    /// OTHER \\\

    internal fun runnerClosed(runner: BaseRunner) {
        runners.remove(runner)
    }

}
package org.jetbrains.dekaf.main.base

import org.jetbrains.dekaf.inter.intf.InterSession
import org.jetbrains.dekaf.main.db.DbQueryRunner
import org.jetbrains.dekaf.main.db.DbSession
import org.jetbrains.dekaf.main.db.DbTransaction
import org.jetbrains.dekaf.main.queries.Query
import org.jetbrains.dekaf.main.queries.QueryLayout
import org.jetbrains.dekaf.main.util.choose


class BaseSession (private val facade: BaseFacade) : DbSession {

    private var interSession: InterSession? = null

    private var activeTransactionPerformer: InTransactionPerformer? = null
    private var beyondTransactionPerformer: BeyondTransactionPerformer? = null

    private var broken: Boolean = false
    private var closed: Boolean = false


    constructor(facade: BaseFacade, interSession: InterSession) : this(facade) {
        this.interSession = interSession

    }


    override fun perform(statementText: String) =
        inside { p ->
            p.perform(statementText)
        }

    override fun <T> query(queryText: String, layout: QueryLayout<T>): DbQueryRunner<T> =
        inside { p -> 
            p.query(queryText, layout)
        }

    override fun <T> query(query: Query<T>): DbQueryRunner<T> =
        inside { p ->
            p.query(query)
        }


    private fun<T> inside(block: (performer: BasePerformer) -> T): T {
        val p: BasePerformer = activeTransactionPerformer ?: ensureBeyondTransaction()
        return block(p)
    }



    override fun beginTransaction(): DbTransaction {
        if (activeTransactionPerformer == null) {
            val t = InTransactionPerformer(this)
            activeTransactionPerformer = t
            return t
        }
        else {
            throw IllegalStateException("Transaction has been already begun")
        }
    }

    internal fun releaseTransaction(transaction: InTransactionPerformer) {
        if (activeTransactionPerformer === transaction) {
            activeTransactionPerformer = null
        }
        else {
            if (transaction.baseSession === this) throw IllegalStateException("Transaction has been already detached")
            else throw IllegalArgumentException("Alien transaction")
        }
    }


    private fun ensureBeyondTransaction(): BeyondTransactionPerformer {
        var bt: BeyondTransactionPerformer? = beyondTransactionPerformer
        if (bt == null) {
            bt = BeyondTransactionPerformer(this)
            beyondTransactionPerformer = bt
        }
        return bt
    }

    override fun ping() {
        ensureInterSession().ping()
    }


    internal fun ensureInterSession(): InterSession {
        val s = interSession
        if (closed || s == null || s.isClosed)
            throw IllegalStateException("DB session is " + broken.choose("broken and closed", "closed"))
        return s
    }


    override fun markBroken() {
        broken = true
        val s = detachInterSession()
        if (s != null) facade.releaseInterSessionBroken(s)
    }

    override fun close() {
        val s = detachInterSession()
        if (s != null) facade.releaseInterSessionBack(s)
    }

    private fun detachInterSession(): InterSession? {
        val s = interSession
        interSession = null
        closed = true
        return s
    }

}
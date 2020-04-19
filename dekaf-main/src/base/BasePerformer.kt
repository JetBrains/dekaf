package org.jetbrains.dekaf.main.base

import org.jetbrains.dekaf.inter.intf.InterSession
import org.jetbrains.dekaf.main.db.DbInsideTransaction
import org.jetbrains.dekaf.main.db.DbTransaction


sealed class BasePerformer : DbInsideTransaction {

    internal val baseSession: BaseSession
    internal val interSession: InterSession


    constructor(session: BaseSession) {
        this.baseSession = session
        this.interSession = baseSession.ensureInterSession()
    }


    override fun perform(statementText: String) {
        interSession.perform(statementText)
    }

}



class InTransactionPerformer : BasePerformer, DbTransaction {

    private var ended: Boolean = false

    constructor(session: BaseSession) : super(session) {
        interSession.beginTransaction()
    }

    override fun commit() {
        interSession.commit()
        ended = true
    }

    override fun rollback() {
        interSession.rollback()
        ended = true
    }

    override fun close() {
        if (!ended) {
            try {
                rollback()
            }
            catch (e: Exception) {
                // TODO pacnic!
            }
        }

        baseSession.releaseTransaction(this)
    }

}


class BeyondTransactionPerformer : BasePerformer {

    constructor(session: BaseSession) : super(session)

}


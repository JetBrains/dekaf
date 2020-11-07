package org.jetbrains.dekaf.main.db


interface DbFacade {

    fun connect()

    fun disconnect()

    fun openSession(): DbSession

    fun isConnected(ping: Boolean = false): Boolean

    fun countActiveSessions(): Int

}



fun<X> DbFacade.inSession(block: (DbSession) -> X): X {
    val session = openSession()
    try {
        return block(session)
    }
    finally {
        session.close()
    }
}


fun<X> DbFacade.inTransaction(block: (DbTransaction) -> X): X =
        this.inSession {
            session -> session.inTransaction(block)
        }


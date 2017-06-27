package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.Rdbms
import org.jetbrains.dekaf.core.ConnectionInfo
import org.jetbrains.dekaf.core.DBFacade
import org.jetbrains.dekaf.core.DBSession
import org.jetbrains.dekaf.core.DBTransaction
import org.jetbrains.dekaf.inter.InterFacade
import java.util.function.Function
import java.lang.IllegalStateException
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.function.Consumer


internal open class BaseFacade: DBFacade {

    /// IMMUTABLE SETTINGS \\\

    val provider: BaseProvider

    val rdbms: Rdbms

    val inter: InterFacade


    /// STATE \\\

    private var connected: Boolean = false

    private val sessions = ConcurrentLinkedDeque<BaseSession>()

    private var prima: BaseSession? = null

    private var connectionInfo_: ConnectionInfo? = null


    /// INITIALIZATION \\\

    internal constructor(provider: BaseProvider, inter: InterFacade) {
        this.provider = provider
        this.rdbms = inter.rdbms
        this.inter = inter
    }

    internal open fun init() {}


    /// API IMPLEMENTATION \\\

    override fun rdbms(): Rdbms {
        return rdbms
    }

    override fun connect() {
        inter.activate()
        prima = leaseSession()
        connectionInfo_ = inter.getConnectionInfo()
        connected = true
    }

    override fun reconnect() {
        TODO("not implemented yet")
    }

    override fun ping(): Int {
        val p = prima
        if (p != null) {
            return p.ping()
        }
        else {
            throw IllegalStateException("No primary session")
        }
    }

    override fun disconnect() {
        closeAllSessions()
        closePrima()
        inter.deactivate()
        connected = false
    }

    private fun closeAllSessions() {
        val sessionsToClose = sessions.toArray(emptyArray<BaseSession>())
        val n = sessionsToClose.size
        for (i in n-1 downTo 0) {
            val session = sessionsToClose[i]
            if (!session.isClosed) closeTheSession(session)
            sessions.remove(session)
        }
    }

    private fun closePrima() {
        val p = prima
        if (p != null) {
            closeTheSession(p)
            prima = null
        }
    }

    private fun closeTheSession(session: BaseSession) {
        try {
            session.close()
        }
        catch(e: Exception) {
            System.err.println("Failed to close session: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    override val isConnected: Boolean
        get() = connected

    override val connectionInfo: ConnectionInfo
        get() = connectionInfo_ ?: throw IllegalStateException("The connection info is not obtained")

    override fun <I : Any?> getSpecificService(serviceClass: Class<I>, serviceName: String): I? {
        TODO("not implemented yet")
    }

    override fun <R : Any?> inTransaction(operation: Function<DBTransaction, R>): R {
        val session = openSession()
        try {
            return session.inTransaction(operation)
        }
        finally {
            session.close()
        }
    }

    override fun inTransactionDo(operation: Consumer<DBTransaction>) {
        val session = openSession()
        try {
            session.inTransactionDo(operation)
        }
        finally {
            session.close()
        }
    }

    override fun <R : Any?> inSession(operation: Function<DBSession, R>): R {
        val session = openSession()
        try {
            return operation.apply(session)
        }
        finally {
            session.close()
        }
    }

    override fun inSessionDo(operation: Consumer<DBSession>) {
        val session = openSession()
        try {
            operation.accept(session)
        }
        finally {
            session.close()
        }
    }

    override fun leaseSession(): BaseSession {
        return openSession()
    }

    private fun openSession(): BaseSession {
        val interSession = inter.openSession()
        val session = BaseSession(this, interSession)
        sessions += session
        return session
    }

    internal fun sessionClosed(session: BaseSession) {
        sessions -= session
        if (prima === session) prima = null
    }
}
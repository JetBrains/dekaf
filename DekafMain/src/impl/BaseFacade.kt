package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.Rdbms
import org.jetbrains.dekaf.core.*
import org.jetbrains.dekaf.inter.InterFacade
import java.lang.IllegalStateException
import java.util.concurrent.ConcurrentLinkedDeque


internal open class BaseFacade: DBFacade {

    /// IMMUTABLE SETTINGS \\\

    val provider: BaseProvider

    val rdbms: Rdbms

    val inter: InterFacade


    /// STATE \\\

    private var connected: Boolean = false

    private val sessions = ConcurrentLinkedDeque<BaseSession>()

    private var prima: BaseSession? = null


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
        connected = true
    }

    override fun reconnect() {
        TODO("not implemented yet")
    }

    override fun ping(): Int {
        val p = prima
        if (p != null) {
            return p.ping().toInt()
        }
        else {
            throw IllegalStateException("No primary session")
        }
    }

    override fun disconnect() {
        closeNormalSessions()
        closePrima()
        inter.deactivate()
        connected = false
    }

    private fun closeNormalSessions() {
        // TODO close all normal sessions
    }

    private fun closePrima() {
        val p = prima
        if (p != null) {
            p.close()
            prima = null
        }
    }

    override val isConnected: Boolean
        get() = TODO("not implemented yet")

    override val connectionInfo: ConnectionInfo
        get() = TODO("not implemented yet")

    override fun <I : Any?> getSpecificService(serviceClass: Class<I>, serviceName: String): I? {
        TODO("not implemented yet")
    }

    override fun <R> inTransaction(operation: InTransaction<R>): R {
        TODO("not implemented yet")
    }

    override fun inTransaction(operation: InTransactionNoResult) {
        TODO("not implemented yet")
    }

    override fun <R> inSession(operation: InSession<R>): R {
        TODO("not implemented yet")
    }

    override fun inSession(operation: InSessionNoResult) {
        TODO("not implemented yet")
    }

    override fun leaseSession(): BaseSession {
        return openSession()
    }

    private fun openSession(): BaseSession {
        val interSession = inter.openSession(null, null, null)
        val session = BaseSession(this, interSession)
        sessions += session
        return session
    }

    internal fun sessionClosed(session: BaseSession) {
        sessions -= session
        if (prima === session) prima = null
    }
}
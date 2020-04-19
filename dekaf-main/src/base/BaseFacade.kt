package org.jetbrains.dekaf.main.base

import org.jetbrains.dekaf.inter.intf.InterFacade
import org.jetbrains.dekaf.inter.intf.InterSession
import org.jetbrains.dekaf.inter.settings.Settings
import org.jetbrains.dekaf.main.db.DbFacade
import org.jetbrains.dekaf.main.pool.ServicePool


class BaseFacade : DbFacade {

    private var interFacade: InterFacade? = null

    private var settings: Settings = Settings.empty

    private var connected: Boolean = false

    private val pool = Pool()



    private inner class Pool : ServicePool<InterSession>() {
        override fun openService(): InterSession = openInterSession()
        override fun closeService(service: InterSession, wasBroken: Boolean) = closeInterSession(service, wasBroken)
    }




    fun setup(interFacade: InterFacade, settings: Settings) {
        if (this.interFacade != null) throw IllegalStateException("The facade is already initialized")

        this.interFacade = interFacade
        this.settings = settings
    }


    override fun connect() {
        connected = true
    }

    override fun disconnect() {
        connected = false
    }


    override fun openSession(): BaseSession {
        val interSession = pool.borrow()
        val session = BaseSession(this, interSession)
        return session
    }

    internal fun releaseInterSessionBack(interSession: InterSession) {
        pool.release(interSession)
    }

    internal fun releaseInterSessionBroken(interSession: InterSession) {
        pool.releaseBroken(interSession)
    }


    override fun isConnected(ping: Boolean): Boolean {
        if (!connected) return false
        if (!ping) return true

        openInterSession().use { interSession ->
            try {
                interSession.ping()
                return true
            }
            catch (e: Exception) {
                return false
            }
        }
    }


    private fun openInterSession(): InterSession {
        val f = interFacade ?: throw IllegalStateException("Facade is not set up")
        return f.openSession()
    }

    private fun closeInterSession(session: InterSession, wasBroken: Boolean) {
        session.rollback()
        session.close()
    }

    override fun countActiveSessions(): Int {
        return pool.activeCount
    }
}
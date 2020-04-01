package org.jetbrains.dekaf.main.base

import org.jetbrains.dekaf.inter.intf.InterFacade
import org.jetbrains.dekaf.inter.intf.InterSession
import org.jetbrains.dekaf.inter.settings.Settings
import org.jetbrains.dekaf.main.db.DbFacade


class BaseFacade : DbFacade {

    private var interFacade: InterFacade? = null

    private var settings: Settings = Settings.empty

    private var connected: Boolean = false


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

    override fun countActiveSessions(): Int {
        return 0
    }
}
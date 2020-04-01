package org.jetbrains.dekaf.main.db


interface DbFacade {

    fun connect()

    fun disconnect()


    fun isConnected(ping: Boolean = false): Boolean

    fun countActiveSessions(): Int

}
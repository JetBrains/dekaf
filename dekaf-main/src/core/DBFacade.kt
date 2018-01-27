package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.Rdbms


/**
 * Represents a database.
 *
 * @author Leonid Bushuev
 */
interface DBFacade : DBSessionAware, ImplementationAccessibleService {


    /**
     * The DBMS this facade is applicable/connected to.
     */
    fun rdbms(): Rdbms

    /**
     * Activates the database driver.
     */
    fun activateDriver();

    /**
     * Connects to the database server.
     */
    fun connect()

    /**
     * Reconnects from the database server.
     */
    fun reconnect()

    /**
     * Ping the server to check whether it's alive.
     * @return respond delay, in ms.
     */
    fun ping(): Int

    /**
     * Disconnects from the database server.
     *
     * If not connected - does nothing.
     */
    fun disconnect()

    /**
     * Deactivates the driver. Also disconnects if necessary.
     */
    fun deactivateDriver();

    /**
     * Checks whether it is connected to the server.
     *
     * TBD does it really performs a "ping" interaction or just returning a kind of internal status?
     *
     * @return whether is connected.
     */
    val isConnected: Boolean

    /**
     * The driver information, when it's loaded and activated.
     * Null when is not activated.
     */
    val driverInfo: DbDriverInfo?

    /**
     * Provides a brief connection info.
     * @return a brief connection info.
     */
    val connectionInfo: ConnectionInfo


    /**
     * Lease a session.
     * When the session is not needed more, the [DBLeasedSession.close] must be called.
     *
     * @return a leased session.
     */
    fun leaseSession(): DBLeasedSession

}

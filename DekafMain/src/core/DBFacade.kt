package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.Rdbms


/**
 * Represents a database.
 *
 * @author Leonid Bushuev
 */
interface DBFacade : ImplementationAccessibleService {


    /**
     * The DBMS this facade is applicable/connected to.
     */
    fun rdbms(): Rdbms


    /**
     * Connects to the database server.
     */
    fun connect()

    /**
     * Reconnects from the database server.
     */
    fun reconnect()

    /**
     * Disconnects from the database server.
     *
     *
     * If not connected - does nothing.
     *
     */
    fun disconnect()

    /**
     * Checks whether it is connected to the server.
     *
     *
     * TBD does it really performs a "ping" interaction or just returning a kind of internal status?
     *
     *
     * @return whether is connected.
     */
    val isConnected: Boolean

    /**
     * Provides a brief connection info.
     * @return a brief connection info.
     */
    val connectionInfo: ConnectionInfo


    /**
     * Performs the given operation in transaction and returns the result.
     *
     * @param operation operation to perform.
     * *
     * @param <R>       type of result.
     * *
     * @return the result.
     * *
     * *
     * @see .inTransaction
     * @see .inSession</R>
     */
    fun <R> inTransaction(operation: InTransaction<R>): R

    /**
     * Performs the given operation in transaction.

     * @param operation   operation to perform.
     * *
     * *
     * @see .inTransaction
     * @see .inSession
     */
    fun inTransaction(operation: InTransactionNoResult)


    /**
     * Performs the given operation in a session and returns the result.
     * @param operation  operation to perform.
     * *
     * @param <R>        type of result.
     * *
     * @return           the result.
     * *
     * *
     * @see .inSession
     * @see .inTransaction</R>
     */
    fun <R> inSession(operation: InSession<R>): R

    /**
     * Performs the given operation in a session.
     * @param operation   operation to perform.
     * *
     * *
     * @see .inSession
     * @see .inTransaction
     */
    fun inSession(operation: InSessionNoResult)


    /**
     * Lease a session.

     * When the session is not needed more, the [DBLeasedSession.close] must be called.

     * @return a leased session.
     */
    fun leaseSession(): DBLeasedSession

}

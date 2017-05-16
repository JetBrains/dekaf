package org.jetbrains.dekaf.core

/**
 * Leased session.

 * @author Leonid Bushuev
 */
interface DBLeasedSession : DBSession, DBTransactionControl {

    /**
     * Checks whether the session is really alive.
     *
     * If the check is successful, just returns the ping duration.
     *
     * If the check failed, closes the session and throws an exception.
     *
     * @return ping duration (in milliseconds).
     *
     * @see .isClosed
     */
    fun ping(): Int

    /**
     * Check whether the session is closed.
     * @return  is closed.
     *
     *
     * @see .ping
     * @see .close
     */
    val isClosed: Boolean

    /**
     * Closes the session.
     *
     * When session is already closed, just does nothing.
     *
     * @see .isClosed
     */
    fun close()

}

package org.jetbrains.dekaf.inter.intf;


import org.jetbrains.dekaf.inter.exceptions.DBConnectionException;



/**
 * Represents one session with one database service.
 */
public interface InterSession extends AutoCloseable {

    /**
     * Check whether the service is alive and accessible.
     */
    void ping()
            throws DBConnectionException;

    /**
     * Closes the session.
     */
    void close();

    /**
     * Checks whether the session was closed.
     */
    boolean isClosed();

}

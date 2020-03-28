package org.jetbrains.dekaf.inter.intf;


import org.jetbrains.annotations.NotNull;
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
     * Opens a new seance.
     */
    @NotNull
    InterSeance openSeance();

    /**
     * Begins a transaction.
     */
    void beginTransaction();

    /**
     * Commits the current transaction.
     */
    void commit();

    /**
     * Rolls the current transaction back.
     */
    void rollback();

    /**
     * Checks whether the session is in a transaction.
     */
    boolean isInTransaction();

    /**
     * Closes the session.
     */
    void close();

    /**
     * Checks whether the session was closed.
     */
    boolean isClosed();

}

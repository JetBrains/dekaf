package org.jetbrains.dekaf.inter.intf;


import org.jetbrains.annotations.NotNull;



/**
 * Represents one session with one database service.
 */
public interface InterSession extends AutoCloseable {

    /**
     * Check whether the service is alive and accessible.
     */
    void ping();

    /**
     * Performs a simple statement one time without parameters or returned cursor.
     * @param statementText the statement to perform.
     */
    void perform(@NotNull String statementText);

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

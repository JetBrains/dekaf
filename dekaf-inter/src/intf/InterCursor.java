package org.jetbrains.dekaf.inter.intf;

public interface InterCursor extends AutoCloseable {

    /**
     * Closes the seance.
     */
    void close();

    /**
     * Check whether the seance was closed.
     * @return whether the seance was closed.
     */
    boolean isClosed();

}

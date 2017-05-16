package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;



public interface InterSession {

    ////// TRANSACTIONS \\\\\\

    void begin();

    void commit();

    void rollback();

    boolean isInTransaction();


    ////// SEANCES \\\\\\

    @NotNull
    InterSeance openSeance();


    ////// OTHER \\\\\\

    int ping();


    ////// CLOSING \\\\\\

    void close();

}

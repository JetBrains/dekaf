package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.ImplementationAccessibleService;



public interface InterSession extends ImplementationAccessibleService {

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

package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.core.ImplementationAccessibleService;
import org.jetbrains.dekaf.core.Settings;



public interface InterFacade extends InterLongService, ImplementationAccessibleService {

    ////// CONNECT-DISCONNECT \\\\\\

    void setUp(@NotNull Settings settings);

    void activate();

    boolean isActive();

    void deactivate();


    ////// SESSIONS \\\\\\

    InterSession openSession();


    ////// OTHER \\\\\\

    @NotNull
    Rdbms getRdbms();

    @NotNull
    ConnectionInfo getConnectionInfo();

}

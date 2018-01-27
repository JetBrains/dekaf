package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.core.ConnectionParameterCategory;
import org.jetbrains.dekaf.core.DbDriverInfo;
import org.jetbrains.dekaf.core.ImplementationAccessibleService;

import java.util.Map;



public interface InterFacade extends ImplementationAccessibleService {

    ////// TUNING \\\\\\

    void setJarsPath(@Nullable String path);

    void setJarsToLoad(@Nullable String[] files);

    void setConnectionString(@Nullable String connectionString);

    void setParameters(@NotNull ConnectionParameterCategory category,
                       @NotNull Map<String,Object> parameters);

    @NotNull
    Rdbms getRdbms();


    ////// CONNECT-DISCONNECT \\\\\\

    void activateDriver();

    void activate();

    boolean isActive();

    void deactivate();

    void deactivateDriver();


    ////// SESSIONS \\\\\\

    InterSession openSession();


    ////// OTHER \\\\\\

    @Nullable
    DbDriverInfo getDriverInfo();

    ConnectionInfo getConnectionInfo();

}

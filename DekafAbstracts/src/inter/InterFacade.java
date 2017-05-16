package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionParameterCategory;
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

    void activate();

    boolean isActive();

    void deactivate();


    ////// SESSIONS \\\\\\

    @NotNull
    InterSession openSession(@Nullable String databaseName,
                             @Nullable String userName,
                             @Nullable String password);

}

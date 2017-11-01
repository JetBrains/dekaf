package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;

import java.util.Collection;
import java.util.Set;



public interface InterProvider {

    ////// RDBMS \\\\\\

    @NotNull
    Set<Rdbms> supportedRdbms();

    boolean supportedConnectionString(@NotNull String connectionString);


    ////// DRIVERS \\\\\\

    void setDriverDirectory(@NotNull String directory);

    @NotNull
    String getDriverDirectory();

    void setDriverJars(@Nullable Collection<String> jars);

    @Nullable
    Collection<String> getDriverJars();


    ////// FACADES AND CONNECTIONS \\\\\\

    @NotNull
    InterFacade createFacade(@NotNull Rdbms rdbms);

    @NotNull
    InterFacade createFacade(@NotNull String connectionString);

}

package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.Settings;

import java.util.Set;



public interface InterProvider extends InterLongService {

    ////// LONG SERVICE \\\\\\

    @Override
    void setUp(final @NotNull Settings settings);

    @Override
    void shutDown();



    ////// RDBMS \\\\\\

    @NotNull
    Set<Rdbms> supportedRdbms();

    boolean supportedConnectionString(@NotNull String connectionString);



    ////// FACADES AND CONNECTIONS \\\\\\

    @NotNull
    InterFacade createFacade(@NotNull Rdbms rdbms);

    @NotNull @Deprecated()
    InterFacade createFacade(@NotNull String connectionString);

}

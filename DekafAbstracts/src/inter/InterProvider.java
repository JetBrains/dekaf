package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Rdbms;

import java.util.Set;



public interface InterProvider {

    @NotNull
    Set<Rdbms> supportsRdbms();

    boolean supportsConnectionString(@NotNull String connectionString);

    @NotNull
    InterFacade createFacade(@NotNull Rdbms rdbms);

    @NotNull
    InterFacade createFacade(@NotNull String connectionString);

}

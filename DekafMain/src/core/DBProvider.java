package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Rdbms;



public interface DBProvider {

    @NotNull
    DBFacade provide(@NotNull Rdbms rdbms);

    @NotNull
    DBFacade provide(@NotNull String connectionString);

}

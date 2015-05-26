package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;

import java.util.Properties;
import java.util.Set;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface DBFederatedProvider {

  /**
   * RDBMS that are supported by (or registered in) this federated provider.
   * @return set of RDBMS markers.
   */
  @NotNull
  Set<Rdbms> supportedRdbms();


  /**
   * Prepares and opens a facade to the specific database.
   * @param connectionString      database connection string.
   * @param connectionProperties  additional connection properties.
   * @param connectionsLimit      how many server connections allowed at the same time.
   * @param connect               to connect (when false, returns unconnected facade).
   * @return                      the prepared facade (not connected).
   */
  @NotNull
  DBFacade openFacade(@NotNull String connectionString,
                      @Nullable Properties connectionProperties,
                      int connectionsLimit,
                      boolean connect);

}

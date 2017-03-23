package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;

import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface DBRdbmsProvider extends ImplementationAccessibleService {

  /**
   * The RDBMS this provider is for.
   * @return the RDBMS marker.
   */
  Rdbms rdbms();


  /**
   * The pattern for connection string that accepts this provider.
   * @return the pattern.
   */
  Pattern connectionStringPattern();


  /**
   * Prepares and opens a facade to the specific database.
   * @param connectionString      database connection string.
   * @param connectionProperties  additional connection properties.
   * @param connectionsLimit      how many server connections allowed at the same time.
   * @param connect               connect immediately (if true, the returned facade is connected).
   * @return                      the prepared facade (not connected).
   */
  @NotNull
  DBFacade openFacade(@NotNull String connectionString,
                      @Nullable Properties connectionProperties,
                      int connectionsLimit,
                      boolean connect);





}

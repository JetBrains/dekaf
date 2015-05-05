package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;

import java.util.Properties;



/**
 * Provides with services specific for one RDBMS.
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface DBInterRdbmsServiceProvider {


  /**
   * The RDBMS this provider is for.
   * @return the RDBMS marker.
   */
  Rdbms rdbms();


  /**
   * Prepares a facade to the specific database.
   * @param connectionString      database connection string.
   * @param connectionProperties  additional connection properties.
   * @param connectionsLimit      how many server connections allowed at the same time.
   * @return                      the prepared facade (not connected).
   */
  DBInterFacade openFacade(@NotNull String connectionString,
                           @Nullable Properties connectionProperties,
                           int connectionsLimit);


  /**
   * Provides with the error recognizer that applicable to this RDBMS.
   * @return an instance of error recognizer (usually a singleton instance).
   */
  DBErrorRecognizer getErrorRecognizer();

}

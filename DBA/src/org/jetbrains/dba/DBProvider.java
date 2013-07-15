package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;



/**
 * Maintains RDBMS types and create {@link DBFacade} instances
 * supplied with necessary drivers, etc.
 *
 * <p>
 *   Usually, each application needs with only one instance of such provider.
 *   So it's a good candidate to place it into an IoC container.
 * </p>
 *
 * @see JdbcDBProvider
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface DBProvider {

  /**
   * Determines the type of RDBMS by the given connection string,
   * loads an appropriate JDBC driver and creates a new DB facade
   * for the given connection string.
   *
   * <p>
   *   The provided DB facade is not connected to a database server yet,
   *   you have to call the {@link DBFacade#connect(String)} to connect.
   * </p>
   *
   * @param connectionString  the connection string (JDBC URL) to the desired database.
   *                          The format of the string varies depends on RDBMS.
   * @return  the new non-connected DB facade.
   */
  @NotNull
  DBFacade provide(@NotNull final String connectionString);

}

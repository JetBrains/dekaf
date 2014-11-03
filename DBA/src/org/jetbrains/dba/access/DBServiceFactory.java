package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.sql.SQL;

import javax.sql.DataSource;
import java.util.regex.Pattern;



/**
 * Factory that creates services for a concrete specific RDBMS.
 *
 * <p>
 *   The implementation must have the default constructor (without parameters).
 *   In most cases, only one instance of his class will be created.
 * </p>
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface DBServiceFactory {

  /**
   * The RDBMS this factory creates services for.
   * @return RDBMS.
   */
  @NotNull
  Rdbms rdbms();

  /**
   * The initial (primary) SQL service.
   *
   * <p>
   *   The specific SQL service can be tunable,
   *   so DBFacade should clone it before tune.
   * </p>
   *
   * @return the initial SQL service.
   */
  @NotNull
  SQL cloneSQL();

  /**
   * Recognizer for RDBMS-specific exceptions.
   * @return error recognizing service.
   */
  @NotNull
  DBErrorRecognizer errorRecognizer();

  /**
   * Pattern for connection strings that the JDBC drivers of this RDBMS accept.
   * @return the connection string pattern.
   */
  @NotNull
  Pattern connectionStringPattern();


  /**
   * Creates a DB facade that can connect to a database
   * using given connections source.
   *
   * <p>
   *   Just created facade is not connected.
   * </p>
   *
   * @param source  connections factory service.
   * @return   just created DB facade.
   */
  @NotNull
  DBFacade createFacade(@NotNull final DataSource source);

}

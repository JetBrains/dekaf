package org.jetbrains.dekaf.jdbc;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Mysql;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;

import static org.jetbrains.dekaf.jdbc.MysqlConsts.FETCH_STRATEGY_AUTO;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class MysqlIntermediateFacade extends JdbcIntermediateFacade {


  @MagicConstant(valuesFromClass = MysqlConsts.class,
                 stringValues = {"FETCH_STRATEGY_AUTO", "FETCH_STRATEGY_ROW", "FETCH_STRATEGY_WHOLE"})
  private byte myFetchStrategy = FETCH_STRATEGY_AUTO;


  public MysqlIntermediateFacade(@NotNull final String connectionString,
                                 @Nullable final Properties connectionProperties,
                                 @NotNull final Driver driver,
                                 final int connectionsLimit,
                                 @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }

  public MysqlIntermediateFacade(@NotNull final DataSource dataSource,
                                 final int connectionsLimit,
                                 @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, exceptionRecognizer);
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return Mysql.RDBMS;
  }

  @Override
  public ConnectionInfo obtainConnectionInfoNatively() {
    return getConnectionInfoSmartly(CONNECTION_INFO_QUERY,
                                    SIMPLE_VERSION_PATTERN, 1,
                                    SIMPLE_VERSION_PATTERN, 1);
  }

  @SuppressWarnings("SpellCheckingInspection")
  private static final String CONNECTION_INFO_QUERY =
      "select database(), schema(), left(user(),instr(concat(user(),'@'),'@')-1)";


  @NotNull
  @Override
  protected MysqlIntermediateSession instantiateSession(@NotNull final Connection connection,
                                                        final boolean ownConnection) {
    return new MysqlIntermediateSession(this, myExceptionRecognizer, connection, ownConnection);
  }


  /**
   * The fetching strategy.
   *
   * <p>
   *   MySQL JDBC driver can fetch rows from server in two ways only: all rows at once or row by row.
   *   So this option specifies how this driver will fetch.
   * </p>
   *
   * <p>
   *   Possible options:
   *   <ul>
   *     <li>{@link MysqlConsts#FETCH_STRATEGY_AUTO}: in pack mode — row by row, in non-pack mode — all rows at once;</li>
   *     <li>{@link MysqlConsts#FETCH_STRATEGY_ROW}: always row by row;</li>
   *     <li>{@link MysqlConsts#FETCH_STRATEGY_WHOLE}: always all rows at once.</li>
   *   </ul>
   * </p>
   *
   * @return the fetching strategy, see {@link MysqlConsts}.
   */
  public byte getFetchStrategy() {
    return myFetchStrategy;
  }

  /**
   * MySQL JDBC driver fetching strategy.
   * See {@link #getFetchStrategy}
   * @param fetchStrategy  the fetching strategy, see {@link MysqlConsts}.
   */
  public void setFetchStrategy(@MagicConstant(valuesFromClass = MysqlConsts.class,
                                              stringValues = {"FETCH_STRATEGY_AUTO", "FETCH_STRATEGY_ROW", "FETCH_STRATEGY_WHOLE"})
                               final byte fetchStrategy) {
    myFetchStrategy = fetchStrategy;
  }
}

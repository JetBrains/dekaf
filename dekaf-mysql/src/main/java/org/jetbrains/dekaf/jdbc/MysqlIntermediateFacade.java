package org.jetbrains.dekaf.jdbc;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Mysql;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;
import org.jetbrains.dekaf.util.Version;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Locale;
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
    String[] env;
    Version serverVersion = null, driverVersion;

    final JdbcIntermediateSession session = openSession();
    try {
      // environment
      env = session.queryOneRow(CONNECTION_INFO_QUERY, 4, String.class);
      String verComment = env == null ? null : env[3];
      if (verComment != null) verComment = verComment.toLowerCase(Locale.ENGLISH);
      if (env == null) env = new String[] {null, null, null};

      // versions
      String rdbmsName, serverVersionStr, driverVersionStr;
      try {
        DatabaseMetaData md = session.getConnection().getMetaData();
        serverVersionStr = md.getDatabaseProductVersion();
        driverVersionStr = md.getDriverVersion();
        rdbmsName = md.getDatabaseProductName();
        if (rdbmsName == null) rdbmsName = session.getConnection().getClass().getName();
      }
      catch (SQLException sqle) {
        throw getExceptionRecognizer().recognizeException(sqle, "getting versions using JDBC metadata");
      }

      if (rdbmsName.equals("MySQL")) {
        int pos = serverVersionStr.indexOf(Mysql.MARIADB_FLAVOUR);
        if (pos != -1) {
          serverVersion = extractVersion(serverVersionStr.substring(pos + Mysql.MARIADB_FLAVOUR.length()), SIMPLE_VERSION_PATTERN, 1);
          if (serverVersion == Version.ZERO) serverVersion = extractVersion(serverVersionStr.substring(0, pos), SIMPLE_VERSION_PATTERN, 1);
          if (serverVersion != Version.ZERO) rdbmsName = Mysql.MARIADB_FLAVOUR;
          else serverVersion = null;
        }
        else if (verComment != null) {
          if (verComment.startsWith("memsql")) {
            rdbmsName = Mysql.MEMSQL_FLAVOUR;
            String[] memVer = session.queryOneRow("select @@memsql_version", 1, String.class);
            if (memVer != null && memVer[0] != null) serverVersion = extractVersion(memVer[0], SIMPLE_VERSION_PATTERN, 1);
          }
          else if (verComment.startsWith("mariadb")) rdbmsName = Mysql.MARIADB_FLAVOUR;
        }
      }

      if (serverVersion == null) serverVersion = extractVersion(serverVersionStr, SIMPLE_VERSION_PATTERN, 1);
      driverVersion = extractVersion(driverVersionStr, SIMPLE_VERSION_PATTERN, 1);

      // ok
      return new ConnectionInfo(rdbmsName, env[0], env[1], env[2], serverVersion, driverVersion);
    }
    finally {
      session.close();
    }
  }

  @SuppressWarnings("SpellCheckingInspection")
  private static final String CONNECTION_INFO_QUERY =
      "select database(), schema(), left(user(),instr(concat(user(),'@'),'@')-1), @@version_comment";


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

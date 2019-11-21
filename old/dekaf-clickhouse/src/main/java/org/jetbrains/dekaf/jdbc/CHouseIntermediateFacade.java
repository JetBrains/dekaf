package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.ClickHouse;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;
import org.jetbrains.dekaf.util.Version;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Pattern;



public class CHouseIntermediateFacade extends JdbcIntermediateFacade {

  public CHouseIntermediateFacade(@NotNull final String connectionString,
                                  @Nullable final Properties connectionProperties,
                                  @NotNull final Driver driver,
                                  final int connectionsLimit,
                                  @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }

  public CHouseIntermediateFacade(@NotNull final DataSource dataSource,
                                  final int connectionsLimit,
                                  boolean ownConnections,
                                  @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, ownConnections, exceptionRecognizer);
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return ClickHouse.RDBMS;
  }


  @Override
  public ConnectionInfo obtainConnectionInfoNatively() {
    String[] env;
    String rdbmsName, driverVersionStr;

    final JdbcIntermediateSession session = openSession();
    try {
      // retrieving all except driver version
      env = session.queryOneRow(CONNECTION_INFO_QUERY, 2, String.class);

      // getting the driver version
      try {
        DatabaseMetaData md = session.getConnection().getMetaData();
        rdbmsName = md.getDatabaseProductName();
        if (rdbmsName == null) rdbmsName = session.getConnection().getClass().getName();
        driverVersionStr = md.getDriverVersion();
      }
      catch (SQLException sqle) {
        throw getExceptionRecognizer().recognizeException(sqle, "getting versions using JDBC metadata");
      }
    }
    finally {
      session.close();
    }

    Version driverVersion =
        extractVersion(driverVersionStr, CHOUSE_VERSION_PATTERN, 1);

    if (env != null) {
      assert env.length == 2;
      String serverVersionStr = env[1];
      Version serverVersion = extractVersion(serverVersionStr, CHOUSE_VERSION_PATTERN, 1);
      return new ConnectionInfo(rdbmsName, null, env[0], null, serverVersion, driverVersion);
    }
    else {
      return new ConnectionInfo(rdbmsName, null, null, null, Version.ZERO, driverVersion);
    }
  }

  private static final Pattern CHOUSE_VERSION_PATTERN =
      Pattern.compile("(\\d{1,2}(\\.\\d{1,10}){1,5})");

  @SuppressWarnings("SpellCheckingInspection")
  private static final String CONNECTION_INFO_QUERY =
      "select currentDatabase(), version()";


}

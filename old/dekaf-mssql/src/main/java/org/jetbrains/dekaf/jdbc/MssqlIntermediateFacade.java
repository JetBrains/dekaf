package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Mssql;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;
import org.jetbrains.dekaf.util.Version;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class MssqlIntermediateFacade extends JdbcIntermediateFacade {

  public MssqlIntermediateFacade(@NotNull final String connectionString,
                                 @Nullable final Properties connectionProperties,
                                 @NotNull final Driver driver,
                                 final int connectionsLimit,
                                 @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }

  public MssqlIntermediateFacade(@NotNull final DataSource dataSource,
                                 final int connectionsLimit,
                                 boolean ownConnections,
                                 @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, ownConnections, exceptionRecognizer);
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return Mssql.RDBMS;
  }

  @Override
  public ConnectionInfo obtainConnectionInfoNatively() {
    String[] env;
    Version serverVersion, driverVersion;

    final JdbcIntermediateSession session = openSession();
    try {
      // environment
      env = session.queryOneRow(CONNECTION_INFO_QUERY, 4, String.class);
      if (env == null) env = new String[] {null, null, null, null};

      // versions
      String rdbmsName, serverVersionStr, driverVersionStr;
      try {
        DatabaseMetaData md = session.getConnection().getMetaData();
        if (env[3] != null && env[3].contains("Azure")) {
          rdbmsName = Mssql.AZURE_FLAVOUR;
        }
        else {
          rdbmsName = md.getDatabaseProductName();
          if (rdbmsName == null) rdbmsName = session.getConnection().getClass().getName();
        }
        serverVersionStr = md.getDatabaseProductVersion();
        driverVersionStr = md.getDriverVersion();
      }
      catch (SQLException sqle) {
        throw getExceptionRecognizer().recognizeException(sqle, "getting versions using JDBC metadata");
      }

      serverVersion =
          extractVersion(serverVersionStr, SIMPLE_VERSION_PATTERN, 1);
      driverVersion =
          extractVersion(driverVersionStr, SIMPLE_VERSION_PATTERN, 1);

      // ok
      return new ConnectionInfo(rdbmsName, env[0], env[1], env[2], serverVersion, driverVersion);
    }
    finally {
      session.close();
    }
  }

  private static final String CONNECTION_INFO_QUERY =
      "select db_name(), schema_name(), original_login(), @@version";


  @NotNull
  @Override
  protected MssqlIntermediateSession instantiateSession(@NotNull final Connection connection,
                                                        final boolean ownConnection) {
    return new MssqlIntermediateSession(this, myExceptionRecognizer, connection, ownConnection);
  }
}

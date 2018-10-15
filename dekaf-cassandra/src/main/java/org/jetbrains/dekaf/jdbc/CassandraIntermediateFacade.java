package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Cassandra;
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
 * @author Liudmila Kornilova
 **/
public class CassandraIntermediateFacade extends JdbcIntermediateFacade {
  private static final String CONNECTION_INFO_QUERY =
      "select release_version from system.local";

  public CassandraIntermediateFacade(@NotNull final String connectionString,
                                     @Nullable final Properties connectionProperties,
                                     @NotNull final Driver driver,
                                     final int connectionsLimit,
                                     @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }

  public CassandraIntermediateFacade(@NotNull final DataSource dataSource,
                                     final int connectionsLimit,
                                     boolean ownConnections,
                                     @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, ownConnections, exceptionRecognizer);
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return Cassandra.RDBMS;
  }

  @NotNull
  @Override
  protected JdbcIntermediateSession instantiateSession(@NotNull Connection connection,
                                                       boolean ownConnection) {
    return new CassandraIntermediateSession(this, myExceptionRecognizer, connection, ownConnection);
  }

  @Nullable
  @Override
  protected ConnectionInfo obtainConnectionInfoNatively() {
    String[] env;
    String rdbmsName, driverVersionStr, schemaName;

    final JdbcIntermediateSession session = openSession();
    try {
      env = session.queryOneRow(CONNECTION_INFO_QUERY, 1, String.class);
      Connection connection = session.getConnection();
      DatabaseMetaData md = connection.getMetaData();
      rdbmsName = md.getDatabaseProductName();
      schemaName = connection.getCatalog();
      if (rdbmsName == null) rdbmsName = connection.getClass().getName();
      driverVersionStr = md.getDriverVersion();
    }
    catch (SQLException sqle) {
      throw getExceptionRecognizer().recognizeException(sqle, "getting versions using JDBC metadata");
    }
    finally {
      session.close();
    }

    Version driverVersion = extractVersion(driverVersionStr, SIMPLE_VERSION_PATTERN, 1);

    if (env != null) {
      assert env.length == 1;
      Version serverVersion = extractVersion(env[0], SIMPLE_VERSION_PATTERN, 1);
      return new ConnectionInfo(rdbmsName, null, schemaName, null, serverVersion, driverVersion);
    }
    else {
      return new ConnectionInfo(rdbmsName, null, schemaName, null, Version.ZERO, driverVersion);
    }
  }
}

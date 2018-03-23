package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.Sybase;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;
import org.jetbrains.dekaf.util.Version;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class SybaseIntermediateFacade extends JdbcIntermediateFacade {

  public SybaseIntermediateFacade(@NotNull final String connectionString,
                                 @Nullable final Properties connectionProperties,
                                 @NotNull final Driver driver,
                                 final int connectionsLimit,
                                 @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }

  public SybaseIntermediateFacade(@NotNull final DataSource dataSource,
                                 final int connectionsLimit,
                                 @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, exceptionRecognizer);
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return Sybase.RDBMS;
  }

  @NotNull
  @Override
  protected SybaseIntermediateSession instantiateSession(@NotNull final Connection connection,
                                                         final boolean ownConnection) {
    return new SybaseIntermediateSession(this, myExceptionRecognizer, connection, ownConnection);
  }


  @Override
  public ConnectionInfo obtainConnectionInfoNatively() {
    String[] env;
    String rdbmsName, driverVersionStr;

    final JdbcIntermediateSession session = openSession();
    try {
      // retrieving all except driver version
      env = session.queryOneRow(CONNECTION_INFO_QUERY, 4, String.class);

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
        extractVersion(driverVersionStr, SIMPLE_VERSION_PATTERN, 1);

    if (env != null) {
      assert env.length == 4;
      String serverVersionStr = env[3];
      Version serverVersion =
          extractVersion(serverVersionStr, SYBASE_ASE_VERSION_PATTERN, 1);
      return new ConnectionInfo(rdbmsName, env[0], env[1], env[2], serverVersion, driverVersion);
    }
    else {
      return new ConnectionInfo(rdbmsName, null, null, null, Version.ZERO, driverVersion);
    }
  }

  @SuppressWarnings("SpellCheckingInspection")
  static final String CONNECTION_INFO_QUERY =
      "select db_name(), user_name(), suser_name(), @@version";

  static final Pattern SYBASE_ASE_VERSION_PATTERN =
      Pattern.compile("/(\\d{2}(\\.\\d{1,3}){1,5})/");

}

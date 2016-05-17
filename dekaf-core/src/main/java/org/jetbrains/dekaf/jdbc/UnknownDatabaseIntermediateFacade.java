package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.exceptions.DBIsNotConnected;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;



/**
 * @author Leonid Bushuev
 **/
public class UnknownDatabaseIntermediateFacade extends JdbcIntermediateFacade {

  @Nullable
  private UnknownDatabaseInfo myUnknownInfo = null;

  public UnknownDatabaseIntermediateFacade(@NotNull final String connectionString,
                                           @Nullable final Properties connectionProperties,
                                           @NotNull final Driver driver,
                                           final int connectionsLimit,
                                           @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }

  public UnknownDatabaseIntermediateFacade(@NotNull final DataSource dataSource,
                                           final int connectionsLimit,
                                           @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, exceptionRecognizer);
  }

  @Override
  public synchronized void connect() {
    super.connect();
    myUnknownInfo = UnknownDatabaseInfoHelper.obtainDatabaseInfo(getConnectionInfo());
  }


  @Override
  public ConnectionInfo getConnectionInfo() {
    ConnectionInfo info = super.getConnectionInfo();
    if (info.rdbmsName.startsWith("DB2")) {
      info = hackDB2ConnectionInfo(info);
    }
    return info;
  }

  @NotNull
  private ConnectionInfo hackDB2ConnectionInfo(ConnectionInfo info) {
    String query = "select rtrim(current_server), rtrim(current schema), rtrim(current_user) from sysibm.sysdummy1";
    String[] env = queryStrings(query, 3);
    info = new ConnectionInfo(info.rdbmsName, env[0], env[1], env[2], info.serverVersion, info.driverVersion);
    return info;
  }

  private String[] queryStrings(final String query, final int columnCount) {
    String[] env;
    JdbcIntermediateSession session = openSession();
    try {
      env = session.queryOneRow(query, columnCount, String.class);
    }
    finally {
      session.close();
    }
    return env;
  }


  @NotNull
  UnknownDatabaseInfo getUnknownInfo() {
    if (myUnknownInfo == null) {
      if (!isConnected()) {
        throw new DBIsNotConnected("Facade is not connected to DB");
      }
      else {
        throw new RuntimeException("The special internal info is not obtained. Possible the Facade was not initialized properly");
      }
    }
    return myUnknownInfo;
  }


  @NotNull
  @Override
  protected JdbcIntermediateSession instantiateSession(@NotNull final Connection connection,
                                                       final boolean ownConnection) {
    return new UnknownDatabaseIntermediateSession(this, getExceptionRecognizer(), connection, ownConnection);
  }

}

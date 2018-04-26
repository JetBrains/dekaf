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
@SuppressWarnings("SpellCheckingInspection")
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
                                           boolean ownConnections,
                                           @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, ownConnections, exceptionRecognizer);
  }

  @Override
  public synchronized void connect() {
    super.connect();
    if (myUnknownInfo == null)
      myUnknownInfo = UnknownDatabaseInfoHelper.obtainDatabaseInfo(this);
  }


  @Override
  public ConnectionInfo obtainConnectionInfoNatively() {
    //if (myUnknownInfo == null)
    //  myUnknownInfo = UnknownDatabaseInfoHelper.obtainDatabaseInfo(this);
    assert myUnknownInfo != null;

    String query = null;
    if (myUnknownInfo.isDB2) query = CONNECTION_INFO_DB2_QUERY;
    if (myUnknownInfo.isHsql) query = CONNECTION_INFO_HSQL_QUERY;

    if (query != null) {
      return getConnectionInfoSmartly(query, SIMPLE_VERSION_PATTERN, 1, SIMPLE_VERSION_PATTERN, 1);
    }
    else {
      return super.obtainConnectionInfoNatively();
    }
  }

  private static final String CONNECTION_INFO_DB2_QUERY =
      "select rtrim(current_server), rtrim(current schema), rtrim(current_user) from sysibm.sysdummy1";

  private static final String CONNECTION_INFO_HSQL_QUERY =
      "select database(), current_schema, current_user from information_schema.schemata limit 1";



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

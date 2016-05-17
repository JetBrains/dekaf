package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

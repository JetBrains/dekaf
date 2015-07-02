package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Oracle;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.exceptions.OracleTimezoneRegionNotFoundException;
import org.jetbrains.jdba.intermediate.DBExceptionRecognizer;
import org.jetbrains.jdba.jdbc.pooling.SimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class OracleIntermediateFacade extends JdbcIntermediateFacade {


  private Boolean myCompatibility1882;


  public OracleIntermediateFacade(@NotNull final String connectionString,
                                  @Nullable final Properties connectionProperties,
                                  @NotNull final Driver driver,
                                  final int connectionsLimit,
                                  @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }

  public OracleIntermediateFacade(@NotNull final DataSource dataSource,
                                  final int connectionsLimit,
                                  @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, exceptionRecognizer);
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return Oracle.RDBMS;
  }


  @Override
  public synchronized void connect() {
    do {
      try {
        super.connect();
      }
      catch (OracleTimezoneRegionNotFoundException otr) {
        if (myCompatibility1882 == null) {
          final DataSource originalDataSource = myPool.getOriginalDataSource();
          if (originalDataSource instanceof SimpleDataSource) {
            // a workaround of a bug inside oracle JDBC driver
            // that occurs when connecting to old versions of oracle
            SimpleDataSource sds = (SimpleDataSource) originalDataSource;
            sds.setConnectionProperty("oracle.jdbc.timezoneAsRegion", "false");
            myCompatibility1882 = Boolean.TRUE;
            continue;
          }
          else {
            myCompatibility1882 = Boolean.FALSE;
          }
        }
      }
      break;
    }
    while (true);
  }

  @NotNull
  @Override
  protected JdbcIntermediateSession instantiateSession(@NotNull final Connection connection,
                                                       final boolean ownConnection) {
    return new OracleIntermediateSession(this, myExceptionRecognizer, connection, ownConnection);
  }

}

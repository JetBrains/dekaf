package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Sqlite;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class SqliteIntermediateFacade extends JdbcIntermediateFacade {

  public SqliteIntermediateFacade(@NotNull final String connectionString,
                                  @Nullable final Properties connectionProperties,
                                  @NotNull final Driver driver,
                                  final int connectionsLimit,
                                  @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }

  public SqliteIntermediateFacade(@NotNull final DataSource dataSource,
                                  final int connectionsLimit,
                                  boolean ownConnections,
                                  @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, ownConnections, exceptionRecognizer);
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return Sqlite.RDBMS;
  }


  @Override
  public ConnectionInfo obtainConnectionInfoNatively() {
    return getConnectionInfoSmartly(CONNECTION_INFO_QUERY,
                                    SIMPLE_VERSION_PATTERN, 1,
                                    SIMPLE_VERSION_PATTERN, 1);
  }

  @NotNull
  @Override
  protected JdbcIntermediateSession instantiateSession(@NotNull Connection connection, boolean ownConnection) {
    return new SqliteIntermediateSession(this, myExceptionRecognizer, connection, ownConnection);
  }

  @SuppressWarnings("SpellCheckingInspection")
  private static final String CONNECTION_INFO_QUERY =
      "select null, 'main', null";


}

package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Postgres;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.core.ConnectionInfo;
import org.jetbrains.jdba.intermediate.DBExceptionRecognizer;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Properties;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class PostgresIntermediateFacade extends JdbcIntermediateFacade {

  public PostgresIntermediateFacade(@NotNull final String connectionString,
                                    @Nullable final Properties connectionProperties,
                                    @NotNull final Driver driver,
                                    final int connectionsLimit,
                                    @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }

  public PostgresIntermediateFacade(@NotNull final DataSource dataSource,
                                    final int connectionsLimit,
                                    @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, exceptionRecognizer);
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return Postgres.RDBMS;
  }


  @Override
  public ConnectionInfo getConnectionInfo() {
    return getConnectionInfoSmartly(CONNECTION_INFO_QUERY,
                                    SIMPLE_VERSION_PATTERN, 1,
                                    SIMPLE_VERSION_PATTERN, 1);
  }

  @SuppressWarnings("SpellCheckingInspection")
  public static final String CONNECTION_INFO_QUERY =
      "select current_database(), current_schema(), current_user";

}

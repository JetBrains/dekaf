package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.Redshift;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Properties;



public class RedshiftIntermediateFacade extends JdbcIntermediateFacade {
  public RedshiftIntermediateFacade(@NotNull final String connectionString,
                                    @Nullable final Properties connectionProperties,
                                    @NotNull final Driver driver,
                                    final int connectionsLimit,
                                    @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }

  public RedshiftIntermediateFacade(@NotNull final DataSource dataSource,
                                    final int connectionsLimit,
                                    @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, exceptionRecognizer);
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return Redshift.RDBMS;
  }

  @Nullable
  @Override
  protected ConnectionInfo obtainConnectionInfoNatively() {
    return getConnectionInfoSmartly(CONNECTION_INFO_QUERY,
                                    SIMPLE_VERSION_PATTERN, 1,
                                    SIMPLE_VERSION_PATTERN, 1);
  }

  @SuppressWarnings("WeakerAccess")
  public static final String CONNECTION_INFO_QUERY =
      "select current_database(), current_schema(), current_user";

}

package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.H2db;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Properties;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class H2dbIntermediateFacade extends JdbcIntermediateFacade {

  public H2dbIntermediateFacade(@NotNull final String connectionString,
                                @Nullable final Properties connectionProperties,
                                @NotNull final Driver driver,
                                final int connectionsLimit,
                                @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }

  public H2dbIntermediateFacade(@NotNull final DataSource dataSource,
                                final int connectionsLimit,
                                @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, exceptionRecognizer);
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return H2db.RDBMS;
  }


  @Override
  public ConnectionInfo obtainConnectionInfoNatively() {
    return getConnectionInfoSmartly(CONNECTION_INFO_QUERY,
                                    SIMPLE_VERSION_PATTERN, 1,
                                    SIMPLE_VERSION_PATTERN, 1);
  }

  @SuppressWarnings("SpellCheckingInspection")
  private static final String CONNECTION_INFO_QUERY =
      "select database(), schema(), nvl(current_user(), user())";


}

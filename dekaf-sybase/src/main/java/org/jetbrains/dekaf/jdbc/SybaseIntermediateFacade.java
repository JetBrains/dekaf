package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.Sybase;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
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
  public ConnectionInfo getConnectionInfo() {
    return getConnectionInfoSmartly(CONNECTION_INFO_QUERY,
                                    SYBASE_ASE_VERSION_PATTERN, 1,
                                    SIMPLE_VERSION_PATTERN, 1);
  }

  @SuppressWarnings("SpellCheckingInspection")
  public static final String CONNECTION_INFO_QUERY =
      "select db_name(), user_name(), suser_name()";

  protected static final Pattern SYBASE_ASE_VERSION_PATTERN =
      Pattern.compile("/(\\d{1,2}(\\.\\d{1,3}){2,5})/");

}

package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.Sybase;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;



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
}

package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Postgre;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.intermediate.DBExceptionRecognizer;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Properties;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class PostgreIntermediateFacade extends JdbcIntermediateFacade {

  public PostgreIntermediateFacade(@NotNull final String connectionString,
                                   @Nullable final Properties connectionProperties,
                                   @NotNull final Driver driver,
                                   final int connectionsLimit,
                                   @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }

  public PostgreIntermediateFacade(@NotNull final DataSource dataSource,
                                   final int connectionsLimit,
                                   @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, exceptionRecognizer);
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return Postgre.RDBMS;
  }

}

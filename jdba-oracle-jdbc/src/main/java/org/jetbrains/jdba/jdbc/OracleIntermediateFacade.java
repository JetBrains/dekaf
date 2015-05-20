package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Oracle;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.intermediate.DBErrorRecognizer;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Properties;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class OracleIntermediateFacade extends JdbcIntermediateFacade {

  public OracleIntermediateFacade(@NotNull final String connectionString,
                                  @Nullable final Properties connectionProperties,
                                  @NotNull final Driver driver,
                                  final int connectionsLimit,
                                  @NotNull final DBErrorRecognizer errorRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, errorRecognizer);
  }

  public OracleIntermediateFacade(@NotNull final DataSource dataSource,
                                  final int connectionsLimit,
                                  @NotNull final DBErrorRecognizer errorRecognizer) {
    super(dataSource, connectionsLimit, errorRecognizer);
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return Oracle.RDBMS;
  }

}

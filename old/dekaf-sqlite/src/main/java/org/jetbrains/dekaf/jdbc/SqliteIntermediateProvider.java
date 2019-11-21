package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Sqlite;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.exceptions.DBInitializationException;
import org.jetbrains.dekaf.exceptions.DBPreparingException;

import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class SqliteIntermediateProvider extends JdbcIntermediateRdbmsProvider {


  //// SETTINGS AND STATE \\\\

  public final static SqliteIntermediateProvider INSTANCE =
          new SqliteIntermediateProvider();


  static final Pattern SQLITE_CONNECTION_STRING_PATTERN =
          Pattern.compile("^jdbc:sqlite:.+$");

  static final String SQLITE_CONNECTION_STRING_EXAMPLE =
          "jdbc:sqlite:memory";

  private static final String SQLITE_DRIVER_CLASS_NAME =
          "org.sqlite.JDBC";



  //// INITIALIZATION \\\\



  @NotNull
  @Override
  protected String getConnectionStringExample() {
    return SQLITE_CONNECTION_STRING_EXAMPLE;
  }

  @Override
  protected Driver loadDriver(final String connectionString) {
    Class<Driver> driverClass = getSimpleAccessibleDriverClass(SQLITE_DRIVER_CLASS_NAME);
    if (driverClass == null) {
      // TODO try to load from jars
    }
    if (driverClass == null) {
      throw new DBInitializationException("Sqlite SQL Driver class not found");
    }

    final Driver driver;
    try {
      driver = driverClass.newInstance();
    }
    catch (Exception e) {
      throw new DBPreparingException("Failed to instantiate driver: "+e.getMessage(), e);
    }

    return driver;
  }



  //// IMPLEMENTATION \\\\



  @NotNull
  @Override
  public Rdbms rdbms() {
    return Sqlite.RDBMS;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return SQLITE_CONNECTION_STRING_PATTERN;
  }

  @Override
  public byte specificity() {
    return SPECIFICITY_NATIVE;
  }


  @NotNull
  @Override
  protected SqliteIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                                       @Nullable final Properties connectionProperties,
                                                       final int connectionsLimit,
                                                       @NotNull final Driver driver) {
    return new SqliteIntermediateFacade(connectionString,
                                        connectionProperties,
                                        driver,
                                        connectionsLimit,
                                        SqliteExceptionRecognizer.INSTANCE);
  }

  @NotNull
  @Override
  public BaseExceptionRecognizer getExceptionRecognizer() {
    return SqliteExceptionRecognizer.INSTANCE;
  }

}

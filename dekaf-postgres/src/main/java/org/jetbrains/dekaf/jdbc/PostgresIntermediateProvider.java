package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Postgres;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.exceptions.DBInitializationException;
import org.jetbrains.dekaf.exceptions.DBPreparingException;

import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class PostgresIntermediateProvider extends JdbcIntermediateRdbmsProvider {


  //// SETTINGS AND STATE \\\\

  public final static PostgresIntermediateProvider INSTANCE =
          new PostgresIntermediateProvider();


  static final Pattern POSTGRES_CONNECTION_STRING_PATTERN =
          Pattern.compile("^jdbc:postgresql://.+$");

  static final String POSTGRES_CONNECTION_STRING_EXAMPLE =
          "jdbc:postgresql:///?OpenSourceSubProtocolOverride=true";

  private static final String POSTGRES_DRIVER_CLASS_NAME =
          "org.postgresql.Driver";



  //// INITIALIZATION \\\\



  @NotNull
  @Override
  protected String getConnectionStringExample() {
    return POSTGRES_CONNECTION_STRING_EXAMPLE;
  }

  @Override
  protected Driver loadDriver() {
    Class<Driver> driverClass = getSimpleAccessibleDriverClass(POSTGRES_DRIVER_CLASS_NAME);
    if (driverClass == null) {
      // TODO try to load from jars
    }
    if (driverClass == null) {
      throw new DBInitializationException("PostgreSQL Driver class not found");
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
    return Postgres.RDBMS;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return POSTGRES_CONNECTION_STRING_PATTERN;
  }

  @Override
  public byte specificity() {
    return SPECIFICITY_NATIVE;
  }


  @NotNull
  @Override
  protected PostgresIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                                         @Nullable final Properties connectionProperties,
                                                         final int connectionsLimit,
                                                         @NotNull final Driver driver) {
    return new PostgresIntermediateFacade(connectionString,
                                          connectionProperties,
                                          driver,
                                          connectionsLimit,
                                          PostgresExceptionRecognizer.INSTANCE);
  }

  @NotNull
  @Override
  public BaseExceptionRecognizer getExceptionRecognizer() {
    return PostgresExceptionRecognizer.INSTANCE;
  }

}

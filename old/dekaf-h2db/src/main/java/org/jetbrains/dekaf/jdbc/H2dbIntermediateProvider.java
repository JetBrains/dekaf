package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.H2db;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.exceptions.DBInitializationException;
import org.jetbrains.dekaf.exceptions.DBPreparingException;

import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class H2dbIntermediateProvider extends JdbcIntermediateRdbmsProvider {


  //// SETTINGS AND STATE \\\\

  public final static H2dbIntermediateProvider INSTANCE =
          new H2dbIntermediateProvider();


  static final Pattern H2DB_CONNECTION_STRING_PATTERN =
          Pattern.compile("^jdbc:h2:.+$");

  static final String H2DB_CONNECTION_STRING_EXAMPLE =
          "jdbc:h2:mem:test";

  private static final String H2DB_DRIVER_CLASS_NAME =
          "org.h2.Driver";



  //// INITIALIZATION \\\\



  @NotNull
  @Override
  protected String getConnectionStringExample() {
    return H2DB_CONNECTION_STRING_EXAMPLE;
  }

  @Override
  protected Driver loadDriver(final String connectionString) {
    Class<Driver> driverClass = getSimpleAccessibleDriverClass(H2DB_DRIVER_CLASS_NAME);
    if (driverClass == null) {
      // TODO try to load from jars
    }
    if (driverClass == null) {
      throw new DBInitializationException("H2dbSQL Driver class not found");
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
    return H2db.RDBMS;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return H2DB_CONNECTION_STRING_PATTERN;
  }

  @Override
  public byte specificity() {
    return SPECIFICITY_NATIVE;
  }


  @NotNull
  @Override
  protected H2dbIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                                        @Nullable final Properties connectionProperties,
                                                        final int connectionsLimit,
                                                        @NotNull final Driver driver) {
    return new H2dbIntermediateFacade(connectionString,
                                         connectionProperties,
                                         driver,
                                         connectionsLimit,
                                         H2dbExceptionRecognizer.INSTANCE);
  }

  @NotNull
  @Override
  public BaseExceptionRecognizer getExceptionRecognizer() {
    return H2dbExceptionRecognizer.INSTANCE;
  }

}

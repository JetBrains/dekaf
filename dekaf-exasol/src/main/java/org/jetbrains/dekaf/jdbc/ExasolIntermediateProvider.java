package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Exasol;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.exceptions.DBInitializationException;
import org.jetbrains.dekaf.exceptions.DBPreparingException;

import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Pattern;



public class ExasolIntermediateProvider extends JdbcIntermediateRdbmsProvider {


  //// SETTINGS AND STATE \\\\

  public final static ExasolIntermediateProvider INSTANCE =
          new ExasolIntermediateProvider();


  static final Pattern EXASOL_CONNECTION_STRING_PATTERN =
          Pattern.compile("^jdbc:exa:.+$");

  static final String EXASOL_CONNECTION_STRING_EXAMPLE =
          "jdbc:exa:localhost:8888";

  private static final String EXASOL_DRIVER_CLASS_NAME =
          "com.exasol.jdbc.EXADriver";



  //// INITIALIZATION \\\\



  @NotNull
  @Override
  protected String getConnectionStringExample() {
    return EXASOL_CONNECTION_STRING_EXAMPLE;
  }

  @Override
  protected Driver loadDriver() {
    Class<Driver> driverClass = getSimpleAccessibleDriverClass(EXASOL_DRIVER_CLASS_NAME);
    if (driverClass == null) {
      // TODO try to load from jars
    }
    if (driverClass == null) {
      throw new DBInitializationException("Exasol SQL Driver class not found");
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
    return Exasol.RDBMS;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return EXASOL_CONNECTION_STRING_PATTERN;
  }

  @Override
  public byte specificity() {
    return SPECIFICITY_NATIVE;
  }


  @NotNull
  @Override
  protected ExasolIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                                       @Nullable final Properties connectionProperties,
                                                       final int connectionsLimit,
                                                       @NotNull final Driver driver) {
    return new ExasolIntermediateFacade(connectionString,
                                        connectionProperties,
                                        driver,
                                        connectionsLimit,
                                        ExasolExceptionRecognizer.INSTANCE);
  }

  @NotNull
  @Override
  public BaseExceptionRecognizer getExceptionRecognizer() {
    return ExasolExceptionRecognizer.INSTANCE;
  }

}

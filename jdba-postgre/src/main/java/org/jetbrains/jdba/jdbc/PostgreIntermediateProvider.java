package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Postgre;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.exceptions.DBInitializationException;
import org.jetbrains.jdba.exceptions.DBPreparingException;

import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class PostgreIntermediateProvider extends JdbcIntermediateRdbmsProvider {


  //// SETTINGS AND STATE \\\\

  public final static PostgreIntermediateProvider INSTANCE =
          new PostgreIntermediateProvider();


  static final Pattern POSTGRE_CONNECTION_STRING_PATTERN =
          Pattern.compile("^jdbc:postgresql://.+$");

  static final String POSTGRE_CONNECTION_STRING_EXAMPLE =
          "jdbc:postgresql:///";

  private static final String POSTGRE_DRIVER_CLASS_NAME =
          "org.postgresql.Driver";



  //// INITIALIZATION \\\\



  @NotNull
  @Override
  protected String getConnectionStringExample() {
    return POSTGRE_CONNECTION_STRING_EXAMPLE;
  }

  @Override
  protected Driver loadDriver() {
    Class<Driver> driverClass = getSimpleAccessibleDriverClass(POSTGRE_DRIVER_CLASS_NAME);
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
    return Postgre.RDBMS;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return POSTGRE_CONNECTION_STRING_PATTERN;
  }

  @Override
  public byte specificity() {
    return SPECIFICITY_NATIVE;
  }


  @NotNull
  @Override
  protected PostgreIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                                        @Nullable final Properties connectionProperties,
                                                        final int connectionsLimit,
                                                        @NotNull final Driver driver) {
    return new PostgreIntermediateFacade(connectionString,
                                         connectionProperties,
                                         driver,
                                         connectionsLimit,
                                         PostgreExceptionRecognizer.INSTANCE);
  }

  @NotNull
  @Override
  public BaseExceptionRecognizer getExceptionRecognizer() {
    return PostgreExceptionRecognizer.INSTANCE;
  }

}

package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.ClickHouse;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.exceptions.DBInitializationException;
import org.jetbrains.dekaf.exceptions.DBPreparingException;

import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Pattern;



public class CHouseIntermediateProvider extends JdbcIntermediateRdbmsProvider {


  //// SETTINGS AND STATE \\\\

  public final static CHouseIntermediateProvider INSTANCE =
          new CHouseIntermediateProvider();


  static final Pattern CHOUSE_CONNECTION_STRING_PATTERN =
          Pattern.compile("^jdbc:clickhouse:.+$");

  static final String CHOUSE_CONNECTION_STRING_EXAMPLE =
          "jdbc:clickhouse://localhost:8123";

  private static final String CHOUSE_DRIVER_CLASS_NAME =
          "ru.yandex.clickhouse.ClickHouseDriver";



  //// INITIALIZATION \\\\



  @NotNull
  @Override
  protected String getConnectionStringExample() {
    return CHOUSE_CONNECTION_STRING_EXAMPLE;
  }

  @Override
  protected Driver loadDriver(final String connectionString) {
    Class<Driver> driverClass = getSimpleAccessibleDriverClass(CHOUSE_DRIVER_CLASS_NAME);
    if (driverClass == null) {
      // TODO try to load from jars
    }
    if (driverClass == null) {
      throw new DBInitializationException("ClickHouse SQL Driver class not found");
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
    return ClickHouse.RDBMS;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return CHOUSE_CONNECTION_STRING_PATTERN;
  }

  @Override
  public byte specificity() {
    return SPECIFICITY_NATIVE;
  }


  @NotNull
  @Override
  protected CHouseIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                                       @Nullable final Properties connectionProperties,
                                                       final int connectionsLimit,
                                                       @NotNull final Driver driver) {
    return new CHouseIntermediateFacade(connectionString,
                                        connectionProperties,
                                        driver,
                                        connectionsLimit,
                                        CHouseExceptionRecognizer.INSTANCE);
  }

  @NotNull
  @Override
  public BaseExceptionRecognizer getExceptionRecognizer() {
    return CHouseExceptionRecognizer.INSTANCE;
  }

}

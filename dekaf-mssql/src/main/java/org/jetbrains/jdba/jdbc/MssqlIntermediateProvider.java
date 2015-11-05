package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Mssql;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.exceptions.DBInitializationException;
import org.jetbrains.jdba.exceptions.DBPreparingException;

import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class MssqlIntermediateProvider extends JdbcIntermediateRdbmsProvider {


  //// SETTINGS AND STATE \\\\

  public final static MssqlIntermediateProvider INSTANCE =
          new MssqlIntermediateProvider();


  /**
   * <p>
   * See <a href="https://msdn.microsoft.com/en-us/library/ms378428(v=sql.110).aspx">MS SQL: Building the Connection URL</a>
   * </p>
   *
   */
  static final Pattern MSSQL_CONNECTION_STRING_PATTERN =
          Pattern.compile("^jdbc:sqlserver:.+$");

  static final String MSSQL_CONNECTION_STRING_EXAMPLE =
          "jdbc:sqlserver://";

  private static final String MSSQL_DRIVER_CLASS_NAME =
          "com.microsoft.sqlserver.jdbc.SQLServerDriver";



  //// INITIALIZATION \\\\



  @NotNull
  @Override
  protected String getConnectionStringExample() {
    return MSSQL_CONNECTION_STRING_EXAMPLE;
  }

  @Override
  protected Driver loadDriver() {
    Class<Driver> driverClass = getSimpleAccessibleDriverClass(MSSQL_DRIVER_CLASS_NAME);
    if (driverClass == null) {
      // TODO try to load from jars
    }
    if (driverClass == null) {
      throw new DBInitializationException("MS SQL Driver class not found");
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
    return Mssql.RDBMS;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return MSSQL_CONNECTION_STRING_PATTERN;
  }

  @Override
  public byte specificity() {
    return SPECIFICITY_NATIVE;
  }


  @NotNull
  @Override
  protected MssqlIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                                      @Nullable final Properties connectionProperties,
                                                      final int connectionsLimit,
                                                      @NotNull final Driver driver) {
    return new MssqlIntermediateFacade(connectionString,
                                       connectionProperties,
                                       driver,
                                       connectionsLimit,
                                       MssqlExceptionRecognizer.INSTANCE);
  }

  @NotNull
  @Override
  public BaseExceptionRecognizer getExceptionRecognizer() {
    return MssqlExceptionRecognizer.INSTANCE;
  }

}

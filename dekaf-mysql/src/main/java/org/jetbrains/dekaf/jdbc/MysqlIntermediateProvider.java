package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Mysql;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.exceptions.DBInitializationException;
import org.jetbrains.dekaf.exceptions.DBPreparingException;

import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class MysqlIntermediateProvider extends JdbcIntermediateRdbmsProvider {


  //// SETTINGS AND STATE \\\\

  public final static MysqlIntermediateProvider INSTANCE =
          new MysqlIntermediateProvider();


  /**
   * <p>
   * See <a href="http://dev.mysql.com/doc/connector-j/en/connector-j-reference-configuration-properties.html">MySQL URL Syntax and Configuration Properties for Connector/J</a>
   * </p>
   *
   * MySQL connection string format:
   * <pre>
   *   jdbc:mysql://[host][,failoverhost...][:port]/[database][?propertyName1][=propertyValue1][&propertyName2][=propertyValue2]...
   *   jdbc:mysql://[host:port],[host:port].../[database][?propertyName1][=propertyValue1][&propertyName2][=propertyValue2]...
   * </pre>
   */
  static final Pattern MYSQL_CONNECTION_STRING_PATTERN =
          Pattern.compile("^jdbc:(?:mysql|mariadb)://.+$");

  static final String MYSQL_CONNECTION_STRING_EXAMPLE =
          "jdbc:mysql:///";

  private static final String MYSQL_DRIVER_CLASS_NAME =
          "com.mysql.jdbc.Driver";



  //// INITIALIZATION \\\\



  @NotNull
  @Override
  protected String getConnectionStringExample() {
    return MYSQL_CONNECTION_STRING_EXAMPLE;
  }

  @Override
  protected Driver loadDriver() {
    Class<Driver> driverClass = getSimpleAccessibleDriverClass(MYSQL_DRIVER_CLASS_NAME);
    if (driverClass == null) {
      // TODO try to load from jars
    }
    if (driverClass == null) {
      throw new DBInitializationException("MySQL Driver class not found");
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
    return Mysql.RDBMS;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return MYSQL_CONNECTION_STRING_PATTERN;
  }

  @Override
  public byte specificity() {
    return SPECIFICITY_NATIVE;
  }


  @NotNull
  @Override
  protected MysqlIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                                        @Nullable final Properties connectionProperties,
                                                        final int connectionsLimit,
                                                        @NotNull final Driver driver) {
    return new MysqlIntermediateFacade(connectionString,
                                       connectionProperties,
                                       driver,
                                       connectionsLimit,
                                       MysqlExceptionRecognizer.INSTANCE);
  }

  @NotNull
  @Override
  public BaseExceptionRecognizer getExceptionRecognizer() {
    return MysqlExceptionRecognizer.INSTANCE;
  }

}

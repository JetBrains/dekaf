package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Oracle;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.exceptions.DBPreparingException;

import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class OracleIntermediateProvider extends JdbcIntermediateRdbmsProvider {


  //// SETTINGS AND STATE \\\\

  static final Pattern ORACLE_CONNECTION_STRING_PATTERN =
          Pattern.compile("^jdbc:oracle:(oci|thin):.*@.+$");

  static final String ORACLE_CONNECTION_STRING_EXAMPLE =
          "jdbc:oracle:thin:@//localhost:1521/XE";

  private static final String ORACLE_DRIVER_CLASS_NAME =
          "oracle.jdbc.driver.OracleDriver";



  //// INITIALIZATION \\\\

  public OracleIntermediateProvider() {
    loadAndRegisterDriverIfNeeded(ORACLE_CONNECTION_STRING_EXAMPLE);
  }


  @Override
  protected Driver loadDriver() {
    Class<Driver> driverClass =
        getSimpleAccessibleDriverClass(ORACLE_DRIVER_CLASS_NAME);
    if (driverClass == null) {
      // TODO try to load from jars
    }
    if (driverClass == null) {
      throw new DBPreparingException("Driver class not found");
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
    return Oracle.RDBMS;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return ORACLE_CONNECTION_STRING_PATTERN;
  }

  @Override
  public byte specificity() {
    return SPECIFICITY_NATIVE;
  }

  @NotNull
  @Override
  protected OracleIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                           @Nullable final Properties connectionProperties,
                                           final int connectionsLimit,
                                           @NotNull final Driver driver) {
    return new OracleIntermediateFacade(connectionString,
                            connectionProperties,
                            driver,
                            connectionsLimit,
                            OracleErrorRecognizer.INSTANCE);
  }

  @NotNull
  @Override
  public BaseErrorRecognizer getErrorRecognizer() {
    return OracleErrorRecognizer.INSTANCE;
  }

}

package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.exceptions.DBPreparingException;
import org.jetbrains.jdba.inter.DBInterFacade;
import org.jetbrains.jdba.inter.DBInterRdbmsServiceProvider;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcInterBaseServiceProvider implements DBInterRdbmsServiceProvider {


  /**
   * Unfortunately, JDBC framework doesn't provide constant for this strange text 8-/
   */
  private static final String DAMN_JDBC_DRIVER_NOT_REGISTERED_ERROR_STATE = "08001";


  @Override
  public DBInterFacade openFacade(@NotNull final String connectionString,
                                  @Nullable final Properties connectionProperties,
                                  final int connectionsLimit) {
    Driver driver = getDriver(connectionString);
    BaseErrorRecognizer errorRecognizer = getErrorRecognizer();
    return new JdbcInterFacade(connectionString, connectionProperties, driver, connectionsLimit, errorRecognizer);
  }


  protected Driver getDriver(@NotNull final String connectionString) {
    try {
      return DriverManager.getDriver(connectionString);
    }
    catch (SQLException sqle) {
      throw getErrorRecognizer().recognizeError(sqle, "DriverManager.getDriver for: " + connectionString);
    }
  }


  protected void loadAndRegisterDriverIfNeeded(final String connectionStringExample) {
    if (!whetherApplicableDriverAlreadyRegistered(connectionStringExample)) {
      Driver driver = loadDriver();
      if (driver != null) {
        registerDriver(driver);
      }
    }
  }


  @Nullable
  abstract Driver loadDriver();


  protected boolean whetherApplicableDriverAlreadyRegistered(@NotNull final String connectionString) {
    try {
      DriverManager.getDriver(connectionString);
      return true;
    }
    catch (SQLException sqle) {
      if (!sqle.getSQLState().equals(DAMN_JDBC_DRIVER_NOT_REGISTERED_ERROR_STATE)) {
        // TODO log it somehow
      }
      return false;
    }
  }


  protected void registerDriver(final Driver driver) {
    try {
      DriverManager.registerDriver(driver);
    }
    catch (SQLException sqle) {
      throw new DBPreparingException("Failed to register JDBC Driver: " + sqle.getMessage());
    }
  }


  protected Class<Driver> getSimpleAccessibleDriverClass(@NotNull final String driverClassName) {
    Class<Driver> driverClass;
    try {
      //noinspection unchecked
      return (Class<Driver>) Class.forName(driverClassName);
    }
    catch (ClassNotFoundException e) {
      return null;
    }
  }


  public abstract BaseErrorRecognizer getErrorRecognizer();

}

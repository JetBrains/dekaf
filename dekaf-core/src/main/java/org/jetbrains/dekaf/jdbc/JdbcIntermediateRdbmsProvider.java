package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBInitializationException;
import org.jetbrains.dekaf.exceptions.DBSessionIsClosedException;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateRdbmsProvider;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static org.jetbrains.dekaf.util.Objects.castTo;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcIntermediateRdbmsProvider implements IntegralIntermediateRdbmsProvider {

  /**
   * Unfortunately, JDBC framework doesn't provide constant for this strange text 8-/
   */
  private static final String DAMN_JDBC_DRIVER_NOT_REGISTERED_ERROR_STATE = "08001";


  @NotNull
  @Override
  public JdbcIntermediateFacade openFacade(@NotNull final String connectionString,
                                           @Nullable final Properties connectionProperties,
                                           final int connectionsLimit) {
    Driver driver = getDriver(connectionString);
    return instantiateFacade(connectionString,
                             connectionProperties,
                             connectionsLimit,
                             driver);
  }


  @NotNull
  public JdbcIntermediateSession wrapConnection(@NotNull final Connection connection, final boolean takeOwnership) {
    BaseExceptionRecognizer exceptionRecognizer = getExceptionRecognizer();
    try {
      if (connection.isClosed()) throw new DBSessionIsClosedException("The given connection is closed.");
      return new JdbcIntermediateSession(null, exceptionRecognizer, connection, takeOwnership);
    }
    catch (SQLException sqle) {
      throw exceptionRecognizer.recognizeException(sqle, "JdbcIntermediateRdbmsProvider.wrapConnection()");
    }
  }


  @NotNull
  protected JdbcIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                                     @Nullable final Properties connectionProperties,
                                                     final int connectionsLimit,
                                                     @NotNull final Driver driver) {
    BaseExceptionRecognizer exceptionRecognizer = getExceptionRecognizer();
    return new JdbcIntermediateFacade(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }


  protected Driver getDriver(@NotNull final String connectionString) {
    Driver driver = JdbcDrivers.findPreferredDriverFor(connectionString);
    if (driver == null) driver = getDriverFromJavaDriverManager(connectionString);
    return driver;
  }

  private Driver getDriverFromJavaDriverManager(final @NotNull String connectionString) {
    tryToLoadDriverIfNeeded(connectionString);
    try {
      List<Driver> drivers = Collections.list(DriverManager.getDrivers());
      Comparator<Driver> comparator = getDriverComparator();
      if (comparator != null) Collections.sort(drivers, comparator);
      return getSuitableDriver(connectionString, drivers);
    }
    catch (SQLException sqle) {
      throw getExceptionRecognizer().recognizeException(sqle,
                                                    "DriverManager.getDriver for: " + connectionString);
    }
  }

  @NotNull
  private Driver getSuitableDriver(@NotNull String connectionString,
                                   @NotNull List<Driver> drivers) throws SQLException {
    for (Driver driver : drivers) {
      if (driver.acceptsURL(connectionString)) return driver;
    }
    throw new SQLException("No suitable driver", "08001");
  }

  protected void tryToLoadDriverIfNeeded(final String connectionString) {
    if (!whetherApplicableDriverAlreadyRegistered(connectionString)) {
      Driver driver = loadDriver(connectionString);
      if (driver != null) {
        registerDriver(driver);
      }
    }
  }

  @Nullable
  protected abstract String getConnectionStringExample();

  @Nullable
  protected abstract Driver loadDriver(final String connectionString);

  @Nullable
  protected Comparator<Driver> getDriverComparator() {
    return null;
  }


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
      throw new DBInitializationException("Failed to register JDBC Driver", sqle);
    }
  }


  protected Class<Driver> getSimpleAccessibleDriverClass(@NotNull final String driverClassName) {
    try {
      final ClassLoader driversClassLoader = JdbcDrivers.getDriversClassLoader();
      //noinspection unchecked
      return (Class<Driver>) Class.forName(driverClassName, true, driversClassLoader);
    }
    catch (ClassNotFoundException e) {
      return null;
    }
  }


  @NotNull
  public abstract BaseExceptionRecognizer getExceptionRecognizer();

  @NotNull
  @Override
  public Class<? extends DBExceptionRecognizer> getExceptionRecognizerClass() {
    DBExceptionRecognizer er = getExceptionRecognizer();
    return er.getClass();
  }

  @Nullable
  @Override
  public <I> I getSpecificService(@NotNull final Class<I> serviceClass,
                                  @NotNull final String serviceName) {
    if (serviceName.equalsIgnoreCase(Names.INTERMEDIATE_SERVICE)) {
      return castTo(serviceClass, this);
    }
    else {
      return null;
    }
  }
}

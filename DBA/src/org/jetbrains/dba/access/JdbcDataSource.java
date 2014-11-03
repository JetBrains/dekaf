package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.errors.DBDriverError;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import static com.google.common.base.Strings.isNullOrEmpty;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcDataSource implements DataSource {

  @NotNull
  private final String myConnectionString;

  @NotNull
  private final Properties myConnectionProperties;

  @NotNull
  private final Driver myDriver;

  @Nullable
  private PrintWriter myLogWriter = null;



  public JdbcDataSource(@NotNull final String connectionString,
                        @Nullable final Properties properties,
                        @NotNull final Driver driver)
          throws DriverDoesNotAcceptConnectionStringException
  {
    checkDriverAcceptsConnectionString(connectionString, driver);
    myConnectionString = connectionString;
    myConnectionProperties = copyOfProperties(properties);
    myDriver = driver;
  }


  private static void checkDriverAcceptsConnectionString(String connectionString, Driver driver)
    throws IllegalArgumentException, DriverDoesNotAcceptConnectionStringException {
    try {
      if (!driver.acceptsURL(connectionString)) throw new DriverDoesNotAcceptConnectionStringException(driver, connectionString);
    }
    catch (SQLException e) {
      throw new DriverDoesNotAcceptConnectionStringException(driver, connectionString, e);
    }
  }

  public static class DriverDoesNotAcceptConnectionStringException extends DBDriverError {

    final SQLException myInnerException;

    DriverDoesNotAcceptConnectionStringException(@NotNull Driver driver, @NotNull String connectionString, @NotNull Exception exception) {
      super(String.format("Looks like the driver %s doesn't accept the given connection string (%s). Exception %s: %s",
                          driver.getClass().getSimpleName(), connectionString, exception.getClass().getSimpleName(), exception.getMessage()),
            exception);
      myInnerException = exception instanceof SQLException ? (SQLException) exception : null;
    }


    DriverDoesNotAcceptConnectionStringException(@NotNull Driver driver, @NotNull String connectionString) {
      super(String.format("Driver %s doesn't accept the given connection string (%s).",
                          driver.getClass().getSimpleName(), connectionString));
      myInnerException = null;
    }
  }


  @NotNull
  private static Properties copyOfProperties(@Nullable final Properties properties) {
    Properties p = new Properties();
    if (properties != null && !properties.isEmpty()) {
      p.putAll(properties);
    }
    return p;
  }


  @Override
  public Connection getConnection() throws SQLException {
    return myDriver.connect(myConnectionString, myConnectionProperties);
  }


  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    if (isNullOrEmpty(username) && isNullOrEmpty(password)) {
      return getConnection();
    }
    else {
      // TODO implement JdbcDataSource.getConnection()
      throw new RuntimeException("Method JdbcDataSource.getConnection(username, password) is not implemented yet.");
    }
  }


  @Nullable
  @Override
  public PrintWriter getLogWriter() {
    return myLogWriter;
  }


  @Override
  public void setLogWriter(@Nullable PrintWriter out) {
    myLogWriter = out;
  }


  @Override
  public void setLoginTimeout(int seconds) {
    // TODO implement JdbcDataSource.setLoginTimeout()
    throw new RuntimeException("Method JdbcDataSource.setLoginTimeout() is not implemented yet.");
  }


  @Override
  public int getLoginTimeout() {
    // TODO implement JdbcDataSource.getLoginTimeout()
    throw new RuntimeException("Method JdbcDataSource.getLoginTimeout() is not implemented yet.");
  }


  @Override
  public <T> T unwrap(Class<T> iface) {
    // TODO implement JdbcDataSource.unwrap()
    throw new RuntimeException("Method JdbcDataSource.unwrap() is not implemented yet.");
  }


  @Override
  public boolean isWrapperFor(Class<?> iface) {
    // TODO implement JdbcDataSource.isWrapperFor()
    throw new RuntimeException("Method JdbcDataSource.isWrapperFor() is not implemented yet.");
  }
}
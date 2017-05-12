package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.ImplementationAccessibleService;
import org.jetbrains.dekaf.util.Objects;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import static java.lang.String.format;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcSimpleDataSource implements DataSource, ImplementationAccessibleService {


  ////// STATE \\\\\\

  @NotNull
  private final String myConnectionString;

  @NotNull
  private final Properties myConnectionProperties;

  @NotNull
  private final Driver myDriver;

  @Nullable
  private PrintWriter myLogWriter;


  ////// CONSTRUCTORS \\\\\\

  public JdbcSimpleDataSource(@NotNull final String connectionString,
                              @Nullable final Properties connectionProperties,
                              @NotNull final Driver driver) {
    myConnectionString = connectionString;
    myConnectionProperties = cloneProperties(connectionProperties);
    myDriver = driver;
  }


  public void setConnectionProperty(@NotNull final String name, @Nullable final String value) {
    if (value != null) {
      myConnectionProperties.setProperty(name, value);
    }
    else {
      myConnectionProperties.remove(name);
    }
  }


  @NotNull
  private static Properties cloneProperties(final @Nullable Properties properties) {
    Properties p = new Properties();
    if (properties != null) p.putAll(properties);
    return p;
  }


  ////// IMPLEMENTATION \\\\\\

  @Override
  public Connection getConnection() throws SQLException {
    return myDriver.connect(myConnectionString, myConnectionProperties);
  }

  @Override
  public Connection getConnection(final String username, final String password) {
    throw new IllegalArgumentException("JdbcSimpleDataSource.getConnection(username, password) is not supported. You can pass credentials via connection string or via connection properties.");
  }

  @Nullable
  @Override
  public PrintWriter getLogWriter() {
    return myLogWriter;
  }

  @Override
  public void setLogWriter(@Nullable final PrintWriter writer) {
    myLogWriter = writer;
  }

  @Override
  public void setLoginTimeout(final int seconds) {
    // TODO implement JdbcSimpleDataSource.setLoginTimeout
    throw new RuntimeException("The JdbcSimpleDataSource.setLoginTimeout has not been implemented yet.");
  }

  @Override
  public int getLoginTimeout() {
    // TODO implement JdbcSimpleDataSource.getLoginTimeout
    throw new RuntimeException("The JdbcSimpleDataSource.getLoginTimeout has not been implemented yet.");
  }

  @Override
  @NotNull
  public <T> T unwrap(@SuppressWarnings("SpellCheckingInspection") final Class<T> iface) {
    if (iface.isAssignableFrom(JdbcSimpleDataSource.class)) {
      //noinspection unchecked
      return (T) this;
    }
    else {
      throw new IllegalArgumentException(format("%s is not a wrapper for %s", JdbcSimpleDataSource.class.getSimpleName(), iface.getName()));
    }
  }

  @Override
  public boolean isWrapperFor(@SuppressWarnings("SpellCheckingInspection") final Class<?> iface) throws SQLException {
    return iface.isAssignableFrom(JdbcSimpleDataSource.class);
  }

  @Nullable
  @Override
  public <I> I getSpecificService(@NotNull final Class<I> serviceClass,
                                  @NotNull final String serviceName) throws ClassCastException {
    if (serviceName.equalsIgnoreCase(Names.JDBC_DRIVER)) {
      return Objects.castTo(serviceClass, myDriver);
    }
    else {
      return null;
    }
  }


  ////// NOT-SUPPORTED STUFF \\\\\\

  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException("The JdbcSimpleDataSource.getParentLogger is not supported by Dekaf 2.0.");
  }
}

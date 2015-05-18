package org.jetbrains.jdba.core1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.util.Version;

import java.sql.Driver;

import static java.lang.String.format;



/**
 * Specifies RDBMS, JDBC directories and drivers for integration testing.
 * Static.
 *
 * @deprecated use {@link org.jetbrains.jdba.TestEnvironment} instead.
 */
@Deprecated
public class TestEnvironment extends org.jetbrains.jdba.TestEnvironment {


  private static Rdbms ourRdbms;
  private static String ourJdbcDriverClassName;
  private static Driver ourJdbcDriver;
  private static Version ourJdbcDriverVersion;
  private static DBServiceFactory ourServiceFactory;
  private static String ourConnectionString;



  public static void setup(@NotNull final DBServiceFactory serviceFactory,
                           @NotNull final Class<? extends Driver> driverClass,
                           @Nullable final String connectionString) {
    setupRdbms(serviceFactory);
    setupJdbcDriver(driverClass);
    setupConnectionString(connectionString);
    printInto();
  }

  private static void setupRdbms(@NotNull final DBServiceFactory serviceFactory) {
    ourRdbms = serviceFactory.rdbms();
    ourServiceFactory = serviceFactory;
  }

  private static void setupJdbcDriver(@NotNull final Class<? extends Driver> driverClass) {
    ourJdbcDriverClassName = driverClass.getName();
    ourJdbcDriver = instantiateDriver(driverClass);
    ourJdbcDriverVersion = Version.of(ourJdbcDriver.getMajorVersion(), ourJdbcDriver.getMinorVersion());
  }

  private static Driver instantiateDriver(final @NotNull Class<? extends Driver> driverClass) {
    try {
      return driverClass.newInstance();
    }
    catch (Exception e) {
      throw new RuntimeException(format("Failed to instantiate driver class %s: encountered %s: %s",
                                        driverClass.getName(), e.getClass().getSimpleName(), e.getMessage()));
    }
  }

  private static void setupConnectionString(@Nullable final String connectionString) {
    if (connectionString != null) {
      ourConnectionString = connectionString;
    }
    else if (ourRdbms != null) {
      ourConnectionString = obtainDefaultConnectionString(ourRdbms);
    }
    else {
      ourConnectionString = null;
    }
  }

  private static String obtainDefaultConnectionString(@NotNull final Rdbms rdbms) {
    return getPossibleConnectionString(rdbms.code.toLowerCase());
  }






  private static void printInto() {
    printValue("================== TEST ENVIRONMENT ================\n", 1);
    printValue("\tRDBMS:               %s\n", ourRdbms);
    printValue("\tJDBC driver class:   %s\n", ourJdbcDriverClassName);
    printValue("\tJDBC driver version: %s\n", ourJdbcDriverVersion);
    printValue("\tConnection string:   %s\n", ourConnectionString);
    printValue("----------------------------------------------------\n", 1);
  }

  private static void printValue(@NotNull final String format, @Nullable final Object value) {
    if (value != null) {
      System.out.printf(format, value);
    }
  }



  @NotNull
  public static DBServiceFactory getServiceFactory() {
    assert ourServiceFactory != null : "The Service Factory is not created";
    return ourServiceFactory;
  }

  @NotNull
  public static Driver getJdbcDriver() {
    assert ourJdbcDriver != null : "The JDBC Driver is not instantiated";
    return ourJdbcDriver;
  }

  @NotNull
  public static String getConnectionString() {
    assert ourConnectionString != null : "The Connection String is unknown";
    return ourConnectionString;
  }
}

package org.jetbrains.jdba.core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.util.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Driver;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;



/**
 * Specifies RDBMS, JDBC directories and drivers for integration testing.
 * Static.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class TestEnvironment {


  public static final String CONNECTION_STRING_VAR = "test-db";
  public static final String CONNECTION_STRINGS_VAR_PREFIX = "test-db-";
  public static final String CONNECTION_STRINGS_FILE = "test-db.properties";

  private static final Properties mySystemProperties = System.getProperties();
  private static final Properties myLocalProperties = readLocalProperties();
  private static final Map<String,String> myEnv = System.getenv();



  private static Rdbms ourRdbms;
  private static File ourJdbcDirectory;
  private static File ourJdbcDriverFile;
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

  @NotNull
  public static String obtainConnectionString() {
    String connectionString = getPossibleConnectionString(getVar(CONNECTION_STRING_VAR, null));
    if (connectionString == null) {
      System.err.print("Failed to obtain Test DB connection string\n");
      System.exit(-128);
    }
    return connectionString;
  }

  @Nullable
  private static String getPossibleConnectionString(final @Nullable String cs) {
    if (cs != null) {
      String[] f = cs.split(":", 2);
      if (f.length == 1) {
        String anotherPropertyName = CONNECTION_STRINGS_VAR_PREFIX + f[0];
        String anotherChance = getVar(anotherPropertyName, null);
        return getPossibleConnectionString(anotherChance);
      }
      else {
        return cs;
      }
    }
    else {
      // TODO try to scan possible strings
      return null;
    }
  }




  @Contract("_,null->_; _,!null->!null")
  public static String getVar(@NotNull final String name, @Nullable final String defaultValue) {
    String value = mySystemProperties.getProperty(name);
    if (value == null) value = myLocalProperties.getProperty(name);
    if (value == null) value = myEnv.get(name);
    if (value == null) value = defaultValue;
    return value;
  }


  private static Properties readLocalProperties() {
    Properties p = new Properties();
    File dir = getCurrentDirectory();
    while (dir != null && dir.isDirectory()) {
      File file = new File(dir, CONNECTION_STRINGS_FILE);
      if (file.exists()) {
        readLocalPropertiesFromFile(p, file);
        break;
      }
      else {
        dir = dir.getParentFile();
      }
    }
    return p;
  }

  @NotNull
  private static File getCurrentDirectory() {
    File dir = new File(".");
    try {
      dir = dir.getCanonicalFile();
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
    return dir;
  }

  private static void readLocalPropertiesFromFile(final Properties p, final File localPropertiesFile) {
    try {
      InputStream is = new FileInputStream(localPropertiesFile);
      try {
        p.load(is);
      }
      finally {
        is.close();
      }
    }
    catch (IOException io) {
      System.err.println("Failed to read file " + CONNECTION_STRINGS_FILE + ": " + io.getMessage());
    }
  }


  private static void printInto() {
    printValue("================== TEST ENVIRONMENT ================\n", 1);
    printValue("\tRDBMS:               %s\n", ourRdbms);
    printValue("\tJDBC dir:            %s\n", ourJdbcDirectory);
    printValue("\tJDBC driver file:    %s\n", ourJdbcDriverFile);
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

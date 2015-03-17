package org.jetbrains.jdba;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;



/**
 * Finds a possible connection string for tests.
 * @author Leonid Bushuev from JetBrains
 */
final class TestEnvironmentProvider {

  private static final String JDBC_DRIVERS_PATH_VAR = "jdbc.drivers.path";
  private static final String JDBC_DRIVERS_DEFAULT_PATH = "jdbc";

  private static final String CONNECTION_STRING_VAR = "test-db";
  private static final String CONNECTION_STRINGS_VAR_PREFIX = "test-db-";
  private static final String CONNECTION_STRINGS_FILE = "test-db.properties";


  @NotNull
  private final Properties mySystemProperties;

  @NotNull
  private final Properties myLocalProperties;

  @NotNull
  private final Map<String,String> myEnv;


  static {
    System.setProperty("java.awt.headless", "true");
  }


  TestEnvironmentProvider() {
    this(System.getProperties(), readLocalProperties(), System.getenv());
  }


  TestEnvironmentProvider(@NotNull final Properties systemProperties,
                          @NotNull Properties localProperties,
                          @NotNull final Map<String, String> env) {
    mySystemProperties = systemProperties;
    myLocalProperties = localProperties;
    myEnv = env;
  }


  private static Properties readLocalProperties() {
    Properties p = new Properties();
    File localPropertiesFile = new File(CONNECTION_STRINGS_FILE);
    if (localPropertiesFile.exists()) {
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
    return p;
  }

  @NotNull
  File obtainJdbcDriversDir() {
    String path = getVar(JDBC_DRIVERS_PATH_VAR, JDBC_DRIVERS_DEFAULT_PATH);
    File dir = new File(path);
    if (!dir.exists()) System.err.printf("JDBC driver path '%s' doesn't exist.\n", dir.getPath());
    else if (!dir.isDirectory()) System.err.printf("JDBC driver path '%s' is not a directory.\n", dir.getPath());
    return dir;
  }

  @NotNull
  String obtainConnectionString() {
    String connectionString = getConnectionString(getVar(CONNECTION_STRING_VAR, null));
    if (connectionString == null) {
      System.err.print("Failed to obtain Test DB connection string\n");
      System.exit(-128);
    }
    return connectionString;
  }

  @Nullable
  private String getConnectionString(final @Nullable String cs) {
    if (cs != null) {
      String[] f = cs.split(":", 2);
      if (f.length == 1) {
        String anotherChance = getVar(CONNECTION_STRINGS_VAR_PREFIX + f[0], null);
        return getConnectionString(anotherChance);
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
  String getVar(@NotNull final String name, @Nullable final String defaultValue) {
    String value = mySystemProperties.getProperty(name);
    if (value == null) value = myLocalProperties.getProperty(name);
    if (value == null) value = myEnv.get(name);
    if (value == null) value = defaultValue;
    return value;
  }

}

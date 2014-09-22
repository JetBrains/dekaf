package org.jetbrains.dba;

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
final class TestConnectionStringProvider {

  private static final String CONNECTION_STRING_VAR = "test-db";
  private static final String CONNECTION_STRINGS_VAR_PREFIX = "test-db-";
  private static final String CONNECTION_STRINGS_FILE = "test-db.properties";


  @NotNull
  private final Properties mySystemProperties;

  @NotNull
  private final Properties myLocalProperties;

  @NotNull
  private final Map<String,String> myEnv;


  TestConnectionStringProvider() {
    this(System.getProperties(), readLocalProperties(), System.getenv());
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


  TestConnectionStringProvider(@NotNull final Properties systemProperties,
                               @NotNull Properties localProperties,
                               @NotNull final Map<String, String> env) {
    mySystemProperties = systemProperties;
    myLocalProperties = localProperties;
    myEnv = env;
  }


  @Nullable
  String findConnectionString() {
    return getConnectionString(getVar(CONNECTION_STRING_VAR));
  }

  @Nullable
  private String getConnectionString(final @Nullable String cs) {
    if (cs != null) {
      String[] f = cs.split(":", 2);
      if (f.length == 1) {
        String anotherChance = getVar(CONNECTION_STRINGS_VAR_PREFIX + f[0]);
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


  @Nullable
  String getVar(@NotNull final String name) {
    String value = mySystemProperties.getProperty(name);
    if (value == null) value = myLocalProperties.getProperty(name);
    if (value == null) value = myEnv.get(name);
    return value;
  }

}

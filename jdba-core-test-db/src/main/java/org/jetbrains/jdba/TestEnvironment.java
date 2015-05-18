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
 * @author Leonid Bushuev from JetBrains
 */
public abstract class TestEnvironment {

  public static final String CONNECTION_STRING_VAR = "test-db";
  public static final String CONNECTION_STRINGS_VAR_PREFIX = "test-db-";
  public static final String CONNECTION_STRINGS_FILE = "test-db.properties";

  private static final Properties mySystemProperties = System.getProperties();
  private static final Properties myLocalProperties = readLocalProperties();
  private static final Map<String,String> myEnv = System.getenv();



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
  protected static String getPossibleConnectionString(final @Nullable String cs) {
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


}

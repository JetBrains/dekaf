package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.core.exceptions.DBDriverException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import static org.jetbrains.jdba.util.Strings.matches;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcDriverSupport {

  private final static List<JdbcDriverDef> myDriverDefs = new CopyOnWriteArrayList<JdbcDriverDef>();
  /*
    Arrays.asList(
      new JdbcDriverDef(org.jetbrains.jdba.rdbms.postgre.Postgre.RDBMS, "^jdbc:postgresql:.*$", "^postgresql-.*[\\-\\.]jdbc\\d?\\.jar$", "org.postgresql.Driver"),
      new JdbcDriverDef(org.jetbrains.jdba.rdbms.oracle.Oracle.RDBMS, "^jdbc:oracle:.*$", "^(ojdbc.*|orai18n)\\.jar$", "oracle.jdbc.driver.OracleDriver"),
      new JdbcDriverDef(org.jetbrains.jdba.rdbms.microsoft.MicrosoftSQL.RDBMS, "^jdbc:sqlserver:.*$", "^sqljdbc4\\.jar$", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
      new JdbcDriverDef(org.jetbrains.jdba.rdbms.microsoft.MicrosoftSQL.RDBMS, "^jdbc:jtds:sqlserver:.*$", "^jtds-.*\\.jar$", "net.sourceforge.jtds.jdbc.Driver"),
      new JdbcDriverDef(org.jetbrains.jdba.rdbms.mysql.MySQL.RDBMS, "^jdbc:mysql:.*$", "^mysql-connector-.*\\.jar$", "com.mysql.jdbc.Driver"),
      new JdbcDriverDef(KnownRdbms.HSQL, "^jdbc:hsqldb:.*$", "^hsqldb\\.jar$", "org.hsqldb.jdbc.JDBCDriver")
    )
  );
  */


  private final LinkedHashSet<File> myJdbcDirs = new LinkedHashSet<File>();
  private final Object myJdbcDirsLock = new Object();

  private final Map<JdbcDriverDef, Driver> myLoadedDrivers =
    new ConcurrentHashMap<JdbcDriverDef, Driver>(myDriverDefs.size());


  private boolean myUsingLoadedDriversOnly = false;



  public JdbcDriverSupport() {
    // JDBC drivers path
    final String theJdbcDriversPath = System.getProperty("jdbc.drivers.path");
    if (theJdbcDriversPath != null) {
      File theJdbcDriversDir = new File(theJdbcDriversPath);
      if (theJdbcDriversDir.isDirectory()) {
        addJdbcDir(theJdbcDriversDir);
      }
      else {
        // TODO log somehow a warning that the specified driver path is bad
      }
    }
  }


  public void addJdbcDir(@NotNull final File dir) {
    synchronized (myJdbcDirsLock) {
      myJdbcDirs.add(dir);
    }
  }


  @NotNull
  public JdbcDataSource createDataSource(@NotNull final String connectionString, @Nullable final Properties connectionProperties)
      throws DBDriverException
  {
    Driver driver = obtainDriver(connectionString);
    return new JdbcDataSource(connectionString, connectionProperties, driver);
  }


  @NotNull
  public Driver obtainDriver(@NotNull final String connectionString)
      throws DBDriverException
  {
    final JdbcDriverDef driverDef = determineDriverDef(connectionString);
    if (driverDef == null) {
      throw new DBDriverException("Failed to determine driver by the following connection string: " + connectionString);
    }

    Driver driver = myLoadedDrivers.get(driverDef);
    if (driver == null) {
      driver = obtainDriver(driverDef, connectionString);
      myLoadedDrivers.put(driverDef, driver);
    }

    return driver;
  }


  @NotNull
  private Driver obtainDriver(@NotNull final JdbcDriverDef driverDef, @NotNull final String connectionString)
      throws DBDriverException
  {
    // WAY 1: check whether the driver just is already loaded
    try {
      final Class<?> driverClass = Class.forName(driverDef.driverClassName);
      final Object driverObject = driverClass.newInstance();
      Driver driver = (Driver) driverObject;
      return driver;
    }
    catch (ClassNotFoundException cnf) {
      // no such class is loaded
      // it's not needed to handle this exception somehow
    }
    catch (Exception e) {
      throw new DBDriverException("Failed to instantiate a JDBC driver: " + e.getMessage(), e);
    }

    // WAY 2: load from the directory list
    final List<File> dirs;
    synchronized (myJdbcDirsLock) {
      dirs = new ArrayList<File>(myJdbcDirs);
    }

    ClassLoader classLoader = loadJdbcJars(dirs, driverDef.usualJarNamePattern);
    if (classLoader != null) {
      try {
        Class driverClass = classLoader.loadClass(driverDef.driverClassName);
        final Object driverObject = driverClass.newInstance();
        return (Driver) driverObject;
      }
      catch (ClassNotFoundException cnfe) {
        // no driver
      }
      catch (InstantiationException e) {
        throw new DBDriverException(
          String.format("Failed to use JDBC driver for %s: could not instantiate a driver class %s.", driverDef.rdbms, driverDef.driverClassName), e);
      }
      catch (IllegalAccessException e) {
        throw new DBDriverException(
          String.format("Failed to use JDBC driver for %s: could not instantiate a driver class %s.", driverDef.rdbms, driverDef.driverClassName), e);
      }
    }

    // WAY 3: load from DB-specific place
    // TODO

    // not found so far
    throw new DBDriverException("No driver for the following connection string: " + connectionString);
  }


  @Nullable
  private ClassLoader loadJdbcJars(Collection<File> dirs, Pattern pattern) {
    ArrayList<URL> jarsToLoad = new ArrayList<URL>(2);
    for (File dir : dirs) {
      final File[] entries = dir.listFiles();
      if (entries == null) continue;
      for (File entry : entries) {
        if (entry.isFile() && pattern.matcher(entry.getName()).matches()) {
          try {
            jarsToLoad.add(entry.toURI().toURL());
          }
          catch (MalformedURLException e) {
            // TODO handle this strange case
          }
        }
      }
    }

    if (jarsToLoad.isEmpty()) return null;

    ClassLoader cl = new URLClassLoader(jarsToLoad.toArray(new URL[jarsToLoad.size()]));
    return cl;
  }


  @Nullable
  public static Rdbms determineRdbms(@NotNull final String connectionString) {
    JdbcDriverDef dd = determineDriverDef(connectionString);
    return dd != null ? dd.rdbms : null;
  }


  @Nullable
  static JdbcDriverDef determineDriverDef(@NotNull final String connectionString) {
    for (JdbcDriverDef def : myDriverDefs) {
      if (matches(connectionString, def.connectionStringPattern)) return def;
    }
    return null;
  }


  void setUsingLoadedDriversOnly(boolean usingLoadedDriversOnly) {
    myUsingLoadedDriversOnly = usingLoadedDriversOnly;
  }
}

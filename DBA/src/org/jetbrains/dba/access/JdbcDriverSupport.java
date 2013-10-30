package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.errors.DBDriverError;

import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import static org.jetbrains.dba.utils.Strings.matches;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcDriverSupport {

  private final static List<JdbcDriverDef> myDriverDefs = new CopyOnWriteArrayList<JdbcDriverDef>(
    Arrays.asList(
      new JdbcDriverDef(Rdbms.POSTGRE, "^jdbc:postgresql:.*$", "^postgresql-.*\\.jdbc\\d?\\.jar$", "org.postgresql.Driver"),
      new JdbcDriverDef(Rdbms.ORACLE, "^jdbc:oracle:.*$", "^(ojdbc.*|orai18n)\\.jar$", "oracle.jdbc.driver.OracleDriver"),
      new JdbcDriverDef(Rdbms.MSSQL, "^jdbc:sqlserver:.*$", "^sqljdbc4\\.jar$", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
      new JdbcDriverDef(Rdbms.MSSQL, "^jdbc:jtds:sqlserver:.*$", "^jtds-.*\\.jar$", "net.sourceforge.jtds.jdbc.Driver"),
      new JdbcDriverDef(Rdbms.MYSQL, "^jdbc:mysql:.*$", "^mysql-connector-.*\\.jar$", "com.mysql.jdbc.Driver"),
      new JdbcDriverDef(Rdbms.HSQL2, "^jdbc:hsqldb:.*$", "^hsqldb\\.jar$", "org.hsqldb.jdbc.JDBCDriver")
    )
  );


  private final List<File> myJdbcDirs =
    new CopyOnWriteArrayList<File>();

  private final Map<JdbcDriverDef, Driver> myLoadedDrivers =
    new ConcurrentHashMap<JdbcDriverDef, Driver>(myDriverDefs.size());


  @NotNull
  Driver obtainDriver(@Nullable final JdbcDriverDef driverDef, @NotNull final String connectionString) {

    if (driverDef == null) {
      // the only we can do now is to try to use the DriverManager as is
      try {
        return DriverManager.getDriver(connectionString);
      }
      catch (SQLException sqle) {
        throw new DBDriverError("Failed to connect to an unknown database: could not instantiate driver for given connection string.", sqle);
      }
    }

    Driver loadedDriver = myLoadedDrivers.get(driverDef);
    if (loadedDriver != null) {
      return loadedDriver;
    }

    ClassLoader classLoader = loadJdbcJars(myJdbcDirs, driverDef.usualJarNamePattern);
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
        throw new DBDriverError(
          String.format("Failed to use JDBC driver for %s: could not instantiate a driver class %s.", driverDef.rdbms, driverDef.driverClassName), e);
      }
      catch (IllegalAccessException e) {
        throw new DBDriverError(
          String.format("Failed to use JDBC driver for %s: could not instantiate a driver class %s.", driverDef.rdbms, driverDef.driverClassName), e);
      }
    }

    // the last way is to try to use DriverManager
    try {
      return DriverManager.getDriver(connectionString);
    }
    catch (SQLException sqle) {
      throw new DBDriverError("Failed to connect to an unknown database: could not instantiate driver for given connection string.", sqle);
    }
  }


  private ClassLoader loadJdbcJars(List<File> dirs, Pattern pattern) {
    // TODO
    return null;
  }


  @Nullable
  static JdbcDriverDef determineDriverDef(@NotNull final String connectionString) {
    for (JdbcDriverDef def : myDriverDefs) {
      if (matches(connectionString, def.connectionStringPattern)) return def;
    }
    return null;
  }




}

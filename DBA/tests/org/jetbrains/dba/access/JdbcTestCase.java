package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.utils.Version;

import java.io.File;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcTestCase {

  static {
    System.setProperty("java.awt.headless", "true");
  }

  static final String JDBC_DRIVERS_PATH_VAR = "jdbc.drivers.path";
  static final String JDBC_DRIVERS_DEFAULT_PATH = "jdbc";


  protected static File getJdbcDriversDir() {
    String jdbcDriversPath = System.getProperty(JDBC_DRIVERS_PATH_VAR, JDBC_DRIVERS_DEFAULT_PATH);
    return new File(jdbcDriversPath);
  }



  public static final class DriverExample {
    @NotNull public final Rdbms rdbms;
    @NotNull public final String description;
    @NotNull public final String sampleConnectionString;
    @NotNull public final Version minVersion;

    public DriverExample(@NotNull Rdbms rdbms, @NotNull String description, @NotNull String sampleConnectionString, @NotNull Version minVersion) {
      this.rdbms = rdbms;
      this.description = description;
      this.sampleConnectionString = sampleConnectionString;
      this.minVersion = minVersion;
    }


    @Override
    public String toString() {
      return description;
    }
  }


  public static final DriverExample[] DRIVER_EXAMPLES = {
    new DriverExample(org.jetbrains.dba.rdbms.postgre.Postgre.RDBMS, "Postgre",      "jdbc:postgresql://localhost/database?user=masha&password=secret",    Version.of(8,2)),
    new DriverExample(org.jetbrains.dba.rdbms.oracle.Oracle.RDBMS,  "Oracle-OCI",   "jdbc:oracle:thin:username/password@host:1521:ServiceName",           Version.of(10)),
    new DriverExample(org.jetbrains.dba.rdbms.oracle.Oracle.RDBMS,  "Oracle-thin",  "jdbc:oracle:thin:username/password@host:1521:ServiceName",           Version.of(10)),
    new DriverExample(org.jetbrains.dba.rdbms.microsoft.MicrosoftSQL.RDBMS,   "MSSQL-native", "jdbc:sqlserver://host\\instanceName:1433;databaseName=MyDB",         Version.of(3)),
    new DriverExample(org.jetbrains.dba.rdbms.microsoft.MicrosoftSQL.RDBMS,   "MSSQL-jtds",   "jdbc:jtds:sqlserver://host:1433/MyDatabase;instanceName=MyInstance", Version.of(1)),
    new DriverExample(org.jetbrains.dba.rdbms.mysql.MySQL.RDBMS,   "MySQL",        "jdbc:mysql://localhost:3306/Rabbit?user=masha&password=secret",      Version.of(2)),
  };

}

package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.utils.Version;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcTestCase {

  static {
    System.setProperty("java.awt.headless", "true");
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
    new DriverExample(Rdbms.POSTGRE, "Postgre",      "jdbc:postgresql://localhost/database?user=masha&password=secret",    Version.of(8,2)),
    new DriverExample(Rdbms.ORACLE,  "Oracle-OCI",   "jdbc:oracle:thin:username/password@host:1521:ServiceName",           Version.of(10)),
    new DriverExample(Rdbms.ORACLE,  "Oracle-thin",  "jdbc:oracle:thin:username/password@host:1521:ServiceName",           Version.of(10)),
    new DriverExample(Rdbms.MSSQL,   "MSSQL-native", "jdbc:sqlserver://host\\instanceName:1433;databaseName=MyDB",         Version.of(3)),
    new DriverExample(Rdbms.MSSQL,   "MSSQL-jtds",   "jdbc:jtds:sqlserver://host:1433/MyDatabase;instanceName=MyInstance", Version.of(1)),
    new DriverExample(Rdbms.MYSQL,   "MySQL",        "jdbc:mysql://localhost:3306/Rabbit?user=masha&password=secret",      Version.of(2)),
  };

}

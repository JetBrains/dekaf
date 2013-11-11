package org.jetbrains.dba.access;


import org.jetbrains.dba.Rdbms;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.Driver;
import java.sql.SQLException;

import static org.testng.Assert.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcDriverSupportTest {


  final JdbcDriverSupport myDriverSupport = new JdbcDriverSupport();



  @DataProvider
  public String[][] connectionStrings() {
    return new String[][] {
      { "jdbc:oracle:thin:username/password@host:1521:ServiceName" },
      { "jdbc:oracle:oci:username/password@Alias" },
      { "jdbc:postgresql://localhost/database?user=masha&password=secret" },
      { "jdbc:sqlserver://host\\instanceName:1433;databaseName=MyDB;integrationSecurity=true" },
      { "jdbc:jtds:sqlserver://host:1433/MyDatabase;instanceName=MyInstance" },
      { "jdbc:mysql://localhost:3306/Rabbit?user=masha&password=secret" },
    };
  }

  @Test(dataProvider = "connectionStrings")
  public void determineRdbmsByConnectionString(String connectionString) {
    final JdbcDriverDef dd = JdbcDriverSupport.determineDriverDef(connectionString);
    assertNotNull(dd);
    assertNotNull(dd.rdbms);
    assertNotEquals(dd.rdbms, Rdbms.UNKNOWN);
  }

  @Test(groups = "jdbc", dataProvider = "connectionStrings")
  public void obtainDriver_main(String connectionString) throws SQLException {

    final Driver driver = myDriverSupport.obtainDriver(connectionString);

    assertTrue(driver.acceptsURL(connectionString));
  }


}

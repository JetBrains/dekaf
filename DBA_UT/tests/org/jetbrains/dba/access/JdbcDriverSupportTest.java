package org.jetbrains.dba.access;


import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.Driver;
import java.sql.SQLException;



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
  public void obtainDriver_main(String connectionString) throws SQLException {

    final Driver driver = myDriverSupport.obtainDriver(connectionString);

    Assert.assertTrue(driver.acceptsURL(connectionString));

  }


}

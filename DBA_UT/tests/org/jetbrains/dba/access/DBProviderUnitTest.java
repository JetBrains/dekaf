package org.jetbrains.dba.access;

import org.jetbrains.dba.Rdbms;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBProviderUnitTest {

  final DBProvider myDBProvider = new JdbcDBProvider();


  @DataProvider
  Object[][] simpleConnectionStrings() {
    return new Object[][] {
      { "jdbc:postgresql://localhost:5432/first_database?user=masha&password=secret", Rdbms.POSTGRE },
      { "jdbc:postgresql://localhost/default_database", Rdbms.POSTGRE },
      { "jdbc:oracle:thin:username/password@//localhost:1521:ServiceName", Rdbms.ORACLE },
      { "jdbc:oracle:oci:@//BüroOraServer", Rdbms.ORACLE },
      { "jdbc:sqlserver://msserver:1433", Rdbms.MSSQL },
      { "jdbc:sqlserver://msserver\\BigInstance:1433;DatabaseName=CoolDB;IntegratedSecurity=true", Rdbms.MSSQL },
      { "jdbc:jtds:sqlserver://msserver:1433/BigDatabase", Rdbms.MSSQL },
      { "jdbc:mysql://localhost/lamp", Rdbms.MYSQL },
    };
  }

  @Test(dataProvider = "simpleConnectionStrings")
  public void get_right_DBFacade(String connectionString, Rdbms rdbms) {
    final DBFacade dbFacade = myDBProvider.provide(connectionString);
    assertNotNull(dbFacade);
    assertEquals(dbFacade.getDbms(), rdbms);
  }

}

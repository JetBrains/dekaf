package org.jetbrains.dba.access;

import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.junit.FineRunner;
import org.jetbrains.dba.junit.TestWithParams;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.jetbrains.dba.junit.Assertions.assertEquals;
import static org.jetbrains.dba.junit.Assertions.assertNotNull;


/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(FineRunner.class)
public class DBProviderUnitTest {

  final DBProvider myDBProvider = new JdbcDBProvider();


  private static final Object[][] SIMPLE_CONNECTION_STRINGS = {
      { "jdbc:postgresql://localhost:5432/first_database?user=masha&password=secret", Rdbms.POSTGRE },
      { "jdbc:postgresql://localhost/default_database", Rdbms.POSTGRE },
      { "jdbc:oracle:thin:username/password@//localhost:1521:ServiceName", Rdbms.ORACLE },
      { "jdbc:oracle:oci:@//BüroOraServer", Rdbms.ORACLE },
      { "jdbc:sqlserver://msserver:1433", Rdbms.MSSQL },
      { "jdbc:sqlserver://msserver\\BigInstance:1433;DatabaseName=CoolDB;IntegratedSecurity=true", Rdbms.MSSQL },
      { "jdbc:jtds:sqlserver://msserver:1433/BigDatabase", Rdbms.MSSQL },
      { "jdbc:mysql://localhost/lamp", Rdbms.MYSQL },
  };

  @TestWithParams(params = "SIMPLE_CONNECTION_STRINGS")
  public void get_right_DBFacade(String connectionString, Rdbms rdbms) {
    final DBFacade dbFacade = myDBProvider.provide(connectionString);
    assertNotNull(dbFacade);
    assertEquals(dbFacade.getDbms(), rdbms);
  }

}

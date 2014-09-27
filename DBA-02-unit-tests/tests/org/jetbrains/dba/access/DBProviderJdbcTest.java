package org.jetbrains.dba.access;

import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.junit.FineRunner;
import org.jetbrains.dba.junit.TestWithParams;
import org.jetbrains.dba.utils.Version;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
@RunWith(FineRunner.class)
public class DBProviderJdbcTest extends JdbcTestCase {

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
  public void provide(String connectionString, Rdbms rdbms) {
    final DBFacade dbFacade = myDBProvider.provide(connectionString);
    assertThat(dbFacade).isNotNull();
    assertThat(dbFacade.getDbms()).isEqualTo(rdbms);
  }

  @TestWithParams(params = "DRIVER_EXAMPLES")
  public void getDriverVersion(DriverExample driverExample) {
    final DBFacade dbFacade = myDBProvider.provide(driverExample.sampleConnectionString);
    Version driverVersion = dbFacade.getDriverVersion();
    assertThat(driverVersion.compareTo(driverExample.minVersion)).isPositive();
  }

}

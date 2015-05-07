package org.jetbrains.jdba.core1;

import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.jdbc.JdbcDBProvider;
import org.jetbrains.jdba.junitft.FineRunner;
import org.jetbrains.jdba.junitft.TestWithParams;
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

  final DBProvider myDBProvider = new JdbcDBProvider(true, getJdbcDriversDir());


  private static final Object[][] SIMPLE_CONNECTION_STRINGS = {
      /*
      { "jdbc:postgresql://localhost:5432/first_database?user=masha&password=secret", org.jetbrains.jdba.rdbms.postgre.Postgre.RDBMS},
      { "jdbc:postgresql://localhost/default_database", org.jetbrains.jdba.rdbms.postgre.Postgre.RDBMS},
      { "jdbc:oracle:thin:username/password@//localhost:1521:ServiceName", org.jetbrains.jdba.rdbms.oracle.Oracle.RDBMS},
      { "jdbc:oracle:oci:@//BüroOraServer", org.jetbrains.jdba.rdbms.oracle.Oracle.RDBMS},
      { "jdbc:sqlserver://msserver:1433", org.jetbrains.jdba.rdbms.microsoft.MicrosoftSQL.RDBMS},
      { "jdbc:sqlserver://msserver\\BigInstance:1433;DatabaseName=CoolDB;IntegratedSecurity=true",
        org.jetbrains.jdba.rdbms.microsoft.MicrosoftSQL.RDBMS},
      { "jdbc:jtds:sqlserver://msserver:1433/BigDatabase", org.jetbrains.jdba.rdbms.microsoft.MicrosoftSQL.RDBMS},
      { "jdbc:mysql://localhost/lamp", org.jetbrains.jdba.rdbms.mysql.MySQL.RDBMS},
      */
  };

  @TestWithParams(params = "SIMPLE_CONNECTION_STRINGS")
  public void provide(String connectionString, Rdbms rdbms) {
    final DBFacade dbFacade = myDBProvider.provide(connectionString, null, 0);
    assertThat(dbFacade).isNotNull();
    assertThat(dbFacade.rdbms()).isSameAs(rdbms);
  }

  /*
  @TestWithParams(params = "DRIVER_EXAMPLES")
  public void getDriverVersion(DriverExample driverExample) {
    final DBFacade dbFacade = myDBProvider.provide(driverExample.sampleConnectionString, null, 1);
    Version driverVersion = dbFacade.getDriverVersion();
    assertThat(driverVersion.compareTo(driverExample.minVersion)).isPositive();
  }
  */

}

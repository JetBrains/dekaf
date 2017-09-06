import org.jetbrains.dekaf.*;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateRdbmsProvider;
import org.jetbrains.dekaf.jdbc.*;
import org.jetbrains.dekaf.junitft.TestSuiteExecutor;
import org.jetbrains.dekaf.util.Providers;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class RunIntegrationTests {


  private static final Map<String,Class> ourJdbcSuites;
  private static final Map<String,Class> ourCoreSuites;


  static {
    System.setProperty("java.awt.headless", "true");
    ourJdbcSuites = new TreeMap<String, Class>(String.CASE_INSENSITIVE_ORDER);
    ourJdbcSuites.put(Postgres.RDBMS.code, PostgresJdbcIntegrationTests.class);
    ourJdbcSuites.put(Oracle.RDBMS.code, OracleJdbcIntegrationTests.class);
    ourJdbcSuites.put(Mssql.RDBMS.code, MssqlJdbcIntegrationTests.class);
    ourJdbcSuites.put(Sybase.RDBMS.code, SybaseJdbcIntegrationTests.class);
    ourJdbcSuites.put(Mysql.RDBMS.code, MysqlJdbcIntegrationTests.class);
    ourJdbcSuites.put(H2db.RDBMS.code, H2dbJdbcIntegrationTests.class);
    ourJdbcSuites.put(Sqlite.RDBMS.code, SqliteJdbcIntegrationTests.class);
    ourJdbcSuites.put(Exasol.RDBMS.code, ExasolJdbcIntegrationTests.class);
    ourJdbcSuites.put(Redshift.RDBMS.code, RedshiftJdbcIntegrationTests.class);
    ourCoreSuites = new TreeMap<String, Class>(String.CASE_INSENSITIVE_ORDER);
    ourCoreSuites.put(Postgres.RDBMS.code, PostgresIntegrationTests.class);
    ourCoreSuites.put(Oracle.RDBMS.code, OracleIntegrationTests.class);
    ourCoreSuites.put(Mssql.RDBMS.code, MssqlIntegrationTests.class);
    ourCoreSuites.put(Sybase.RDBMS.code, SybaseIntegrationTests.class);
    ourCoreSuites.put(Mysql.RDBMS.code, MysqlIntegrationTests.class);
    ourCoreSuites.put(H2db.RDBMS.code, H2dbIntegrationTests.class);
    ourCoreSuites.put(Sqlite.RDBMS.code, SqliteIntegrationTests.class);
    ourCoreSuites.put(Exasol.RDBMS.code, ExasolIntegrationTests.class);
    ourCoreSuites.put(Redshift.RDBMS.code, RedshiftIntegrationTests.class);
  }


  public static void main(String[] args) {
    String arg1 = args.length >= 1 ? args[0] : "\0";
    if (arg1.equalsIgnoreCase("unit")) {
      runUnitTests();
    }
    else {
      runIntegrationTests();
    }
  }

  private static void runUnitTests() {
    printBanner(
        "Unit Tests",
        "Java version: " + System.getProperty("java.version"),
        "JUnit version: " + junit.runner.Version.id()
    );

    TestSuiteExecutor.run(UnitTests.class);
  }

  private static void runIntegrationTests() {
    String connectionString = TestEnvironment.obtainConnectionString();

    final Collection<IntegralIntermediateRdbmsProvider> providers =
        Providers.loadAllProviders(IntegralIntermediateRdbmsProvider.class, null);

    byte specificity = Byte.MAX_VALUE;
    IntegralIntermediateRdbmsProvider provider = null;
    for (IntegralIntermediateRdbmsProvider p : providers) {
      if (p.connectionStringPattern().matcher(connectionString).matches()) {
        if (p.specificity() < specificity) {
          specificity = p.specificity();
          provider = p;
        }
      }
    }

    if (provider == null) {
      System.err.println("Unknown which RDBMS supports this connections string: \n\t" + connectionString);
      System.exit(-128);
      return;
    }

    final Rdbms rdbms = provider.rdbms();

    printBanner(
        "RDBMS: " + rdbms.toString(),
        "Java version: " + System.getProperty("java.version"),
        "JUnit version: " + junit.runner.Version.id()
    );

    Class suite1 = ourJdbcSuites.get(rdbms.code);
    Class suite2 = ourCoreSuites.get(rdbms.code);

    if (suite1 == null || suite2 == null) {
      if (suite1 == null) System.err.println("No JDBC tests for " + rdbms.code);
      if (suite2 == null) System.err.println("No integration tests for " + rdbms.code);
      System.exit(-127);
    }

    TestSuiteExecutor.run(suite1);

    printBanner("Direct Connection");
    TestDB.connect();

    TestSuiteExecutor.suiteParameter = "direct";
    TestSuiteExecutor.run(suite2);

    printBanner("Pseudo-remote Connection");
    TestDB.reinitDB(true);

    TestSuiteExecutor.suiteParameter = "remote";
    TestSuiteExecutor.run(suite2);
  }


  private static final String DIV1 =
          "================================================================================\n";
  private static final String DIV2 =
          "--------------------------------------------------------------------------------\n";

  private static void printBanner(final String... text) {
    StringBuilder b = new StringBuilder();
    b.append(DIV1);
    for (String textLine : text) b.append(textLine).append('\n');
    b.append(DIV2);
    System.out.println(b.toString());
  }

}

import com.google.common.collect.ImmutableMap;
import org.jetbrains.jdba.*;
import org.jetbrains.jdba.intermediate.IntegralIntermediateRdbmsProvider;
import org.jetbrains.jdba.jdbc.H2dbJdbcIntegrationTests;
import org.jetbrains.jdba.jdbc.MysqlJdbcIntegrationTests;
import org.jetbrains.jdba.jdbc.OracleJdbcIntegrationTests;
import org.jetbrains.jdba.jdbc.PostgreJdbcIntegrationTests;
import org.jetbrains.jdba.junitft.TestSuiteExecutor;
import org.jetbrains.jdba.util.Providers;

import java.util.Collection;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class RunIntegrationTests {


  private static final ImmutableMap<String,Class> ourJdbcSuites =
          ImmutableMap.<String,Class>builder()
                            .put(Postgre.RDBMS.code, PostgreJdbcIntegrationTests.class)
                            .put(Oracle.RDBMS.code, OracleJdbcIntegrationTests.class)
                            .put(Mysql.RDBMS.code, MysqlJdbcIntegrationTests.class)
                            .put(H2db.RDBMS.code, H2dbJdbcIntegrationTests.class)
                            .build();

  private static final ImmutableMap<String,Class> ourCoreSuites =
          ImmutableMap.<String,Class>builder()
                            .put(Postgre.RDBMS.code, PostgreIntegrationTests.class)
                            .put(Oracle.RDBMS.code, OracleIntegrationTests.class)
                            .put(Mysql.RDBMS.code, MysqlIntegrationTests.class)
                            .put(H2db.RDBMS.code, H2dbIntegrationTests.class)
                            .build();



  static {
    System.setProperty("java.awt.headless", "true");
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

    TestSuiteExecutor.run(AllUnitTestSuites.class);
  }

  private static void runIntegrationTests() {
    String connectionString = TestEnvironment.obtainConnectionString();

    final Collection<IntegralIntermediateRdbmsProvider> providers =
        Providers.loadAllProviders(IntegralIntermediateRdbmsProvider.class);

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

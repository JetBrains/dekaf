import com.google.common.collect.ImmutableMap;
import org.jetbrains.jdba.*;
import org.jetbrains.jdba.jdbc.OracleJdbcIntegrationTests;
import org.jetbrains.jdba.jdbc.PostgreJdbcIntegrationTests;
import org.jetbrains.jdba.junitft.TestSuiteExecutor;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class RunIntegrationTests {


  private static final ImmutableMap<String,Class> ourJdbcSuites =
          ImmutableMap.<String,Class>builder()
                            .put(Postgre.RDBMS.code, PostgreJdbcIntegrationTests.class)
                            .put(Oracle.RDBMS.code, OracleJdbcIntegrationTests.class)
                            .build();

  private static final ImmutableMap<String,Class> ourCoreSuites =
          ImmutableMap.<String,Class>builder()
                            .put(Postgre.RDBMS.code, PostgreIntegrationTests.class)
                            .put(Oracle.RDBMS.code, OracleIntegrationTests.class)
                            .build();



  static {
    System.setProperty("java.awt.headless", "true");
  }


  public static void main(String[] args) {

    final Rdbms rdbms;

    String connectionString = TestEnvironment.obtainConnectionString();
    if (connectionString.startsWith("jdbc:postgresql:")) {
      rdbms = Postgre.RDBMS;
    }
    else if (connectionString.startsWith("jdbc:oracle:")) {
      rdbms = Oracle.RDBMS;
    }
    else {
      System.err.println("Unknown which RDBMS supports this connections string: \n\t" + connectionString);
      System.exit(128);
      return;
    }

    printBanner(
        "RDBMS: " + rdbms.toString(),
        "Java version: " + System.getProperty("java.version"),
        "JUnit version: " + junit.runner.Version.id()
    );

    Class suite1 = ourJdbcSuites.get(rdbms.code);
    Class suite2 = ourCoreSuites.get(rdbms.code);

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

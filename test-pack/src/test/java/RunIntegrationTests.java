import com.google.common.collect.ImmutableMap;
import org.jetbrains.jdba.Oracle;
import org.jetbrains.jdba.Postgre;
import org.jetbrains.jdba.jdbc.OracleJdbcIntegrationTests;
import org.jetbrains.jdba.jdbc.PostgreJdbcIntegrationTests;
import org.jetbrains.jdba.junitft.TestSuiteExecutor;
import org.jetbrains.jdba.oracle.OracleIntegrationTests;
import org.jetbrains.jdba.postgre.PostgreIntegrationTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class RunIntegrationTests {


  @RunWith(Suite.class)
  @Suite.SuiteClasses({
                              PostgreJdbcIntegrationTests.class,
                              PostgreIntegrationTests.class
  })
  public static final class PostgreSuite {}

  @RunWith(Suite.class)
  @Suite.SuiteClasses({
                              OracleJdbcIntegrationTests.class,
                              OracleIntegrationTests.class
  })
  public static final class OracleSuite {}



  private static final ImmutableMap<String,Class> ourJdbcSuites =
          ImmutableMap.<String,Class>builder()
                            .put(Postgre.RDBMS.code, PostgreJdbcIntegrationTests.class)
                            .put(Oracle.RDBMS.code, OracleJdbcIntegrationTests.class)
                            .build();

  private static final ImmutableMap<String,Class> ourLegacySuites =
          ImmutableMap.<String,Class>builder()
                            .put(Postgre.RDBMS.code, PostgreIntegrationTests.class)
                            .put(Oracle.RDBMS.code, OracleIntegrationTests.class)
                            .build();


  static {
    System.setProperty("java.awt.headless", "true");
  }


  public static void main(String[] args) {

    if (args.length == 0) {
      System.out.println("Please specify the RDBMS code\n");
      return;
    }

    String dbmsCode = args[0];
    if (dbmsCode == null || dbmsCode.length() == 0) {
      System.out.println("Please specify the correct RDBMS code\n");
      return;
    }

    dbmsCode = dbmsCode.trim().toUpperCase();
    System.out.println("Testing " + dbmsCode);

    Class suite1 = ourJdbcSuites.get(dbmsCode);
    Class suite2 = ourLegacySuites.get(dbmsCode);

    TestSuiteExecutor.run(suite1, suite2);
  }

}

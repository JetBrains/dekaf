import org.jetbrains.jdba.junitft.TestSuiteExecutor;
import org.jetbrains.jdba.postgre.PostgreIntegrationTests;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class RunIntegrationTests {

  public static void main(String[] args) {
    TestSuiteExecutor.run(PostgreIntegrationTests.class);
  }

}

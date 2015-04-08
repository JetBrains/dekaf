import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.jetbrains.jdba.junitft.TestSuiteExecutor;
import org.jetbrains.jdba.oracle.Oracle;
import org.jetbrains.jdba.oracle.OracleIntegrationTests;
import org.jetbrains.jdba.postgre.Postgre;
import org.jetbrains.jdba.postgre.PostgreIntegrationTests;
import org.jetbrains.jdba.utils.CaseInsensitiveStringComparator;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class RunIntegrationTests {


  private static final ImmutableMap<String,Class> ourSuites =
          ImmutableSortedMap.<String,Class>orderedBy(CaseInsensitiveStringComparator.instance)
                            .put(Postgre.RDBMS.code, PostgreIntegrationTests.class)
                            .put(Oracle.RDBMS.code, OracleIntegrationTests.class)
                            .build();



  public static void main(String[] args) {

    if (args.length == 0) {
      System.out.println("Please specify the RDBMS code\n");
      return;
    }

    String dbmsCode = args[0];
    Class suite = ourSuites.get(dbmsCode);
    if (suite == null) {
      System.out.println("I don't know how to test "+dbmsCode);
    }

    TestSuiteExecutor.run(suite);
  }

}

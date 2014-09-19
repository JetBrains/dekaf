import org.jetbrains.dba.access.DBProviderUnitTest;
import org.jetbrains.dba.access.JdbcDriverSupportTest;
import org.jetbrains.dba.access.StructRowFetcherTest;
import org.jetbrains.dba.access.ValueGetterTest;
import org.jetbrains.dba.sql.OraSQLTest;
import org.jetbrains.dba.sql.SQLScriptBuilderTest;
import org.jetbrains.dba.sql.SQLScriptTest;
import org.jetbrains.dba.sql.SQLTest;
import org.jetbrains.dba.utils.NumberUtilsTest;
import org.jetbrains.dba.utils.StringsTest;
import org.jetbrains.dba.utils.VersionTest;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * JUnit configuration stuff.
 * @author Leonid Bushuev from JetBrains
 */
public class Tests {

  /**
   * Pure unit tests - tests that don't require DB connections and even JDBC drivers.
   */
  @RunWith(Suite.class)
  @Suite.SuiteClasses({
  // ------------------------------------------ \\
                        NumberUtilsTest.class,
                        StringsTest.class,
                        VersionTest.class,
                        SQLTest.class,
                        SQLScriptBuilderTest.class,
                        SQLScriptTest.class,
                        OraSQLTest.class,
                        ValueGetterTest.class,
                        StructRowFetcherTest.class
  // ------------------------------------------ \\
                      })
  public static class UnitTestsSuite {}



  /**
   * JDBC tests - they require JDBC drivers downloaded,
   * but the don't require DB connections.
   */
  @RunWith(Suite.class)
  @Suite.SuiteClasses({
  // ------------------------------------------ \\
                        JdbcDriverSupportTest.class,
                        DBProviderUnitTest.class
  // ------------------------------------------ \\
                      })
  public static class JdbcTestsSuite {}



  /**
   * Runs unit tests from command line.
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      JUnitCore.main(UnitTestsSuite.class.getName());
    }
    else {
      String[] args2 = new String[args.length];
      for (int i = 0; i < args.length; i++) {
        String arg = args[i];
        if (arg.equalsIgnoreCase(UnitTestsSuite.class.getSimpleName())) arg = UnitTestsSuite.class.getName();
        if (arg.equalsIgnoreCase(JdbcTestsSuite.class.getSimpleName())) arg = JdbcTestsSuite.class.getName();
        args2[i] = arg;
      }
      JUnitCore.main(args2);
    }
  }


}

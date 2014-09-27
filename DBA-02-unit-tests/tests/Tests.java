import junit.runner.Version;
import org.jetbrains.dba.access.DBProviderJdbcTest;
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
import org.junit.internal.JUnitSystem;
import org.junit.internal.RealSystem;
import org.junit.internal.TextListener;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.Suite;

import java.util.ArrayList;
import java.util.List;



/**
 * JUnit configuration stuff.
 * @author Leonid Bushuev from JetBrains
 */
public class Tests {


  private static final String TC_DETECT_VAR_NAME = "TEAMCITY_PROJECT_NAME";


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
                        DBProviderJdbcTest.class
  // ------------------------------------------ \\
                      })
  public static class JdbcTestsSuite {}



  /**
   * Reports test runs to TeamCity using TC service messages.
   * See <a href="http://confluence.jetbrains.com/display/TCD8/Build+Script+Interaction+with+TeamCity#BuildScriptInteractionwithTeamCity-ServiceMessages">Interaction with TeamCity</a> for details.
   */
  private static class MyListener extends RunListener {

    @Override
    public void testStarted(Description d) throws Exception {
      say("##teamcity[testStarted name='%s' captureStandardOutput='true']", getTestName(d));
    }

    @Override
    public void testFailure(Failure f) throws Exception {
      Description d = f.getDescription();
      say("##teamcity[testFailed name='%s' message='%s' details='%s']", getTestName(d), f.getMessage(), f.getTrace());
    }

    @Override
    public void testIgnored(Description d) throws Exception {
      say("##teamcity[testIgnored name='%s'", getTestName(d));
    }

    @Override
    public void testFinished(Description d) throws Exception {
      say("##teamcity[testFinished name='%s']", getTestName(d));
    }

    private String getTestName(Description d) {
      return d.getClassName() + "." + d.getMethodName();
    }
  }



  /**
   * Runs unit tests from command line.
   */
  public static void main(String[] args) {

    // header
    System.out.println("Java version: " + System.getProperty("java.version"));
    System.out.println("JUnit version: " + Version.id());
    boolean underTC = System.getenv(TC_DETECT_VAR_NAME) != null;
    if (underTC) System.out.println("TeamCity detected :)");

    // gather suites
    List<Class> suites = new ArrayList<Class>();
    for (String arg : args) {
      if (arg.equalsIgnoreCase(UnitTestsSuite.class.getSimpleName())) suites.add(UnitTestsSuite.class);
      else if (arg.equalsIgnoreCase(JdbcTestsSuite.class.getSimpleName())) suites.add(JdbcTestsSuite.class);
      else System.err.println("Suite "+arg+" not found");
    }
    if (args.length == 0) {
      suites.add(UnitTestsSuite.class);
    }

    // prepare junit
    JUnitSystem system = new RealSystem();
    JUnitCore core = new JUnitCore();
    RunListener listener = underTC ? new MyListener() : new TextListener(system);
    core.addListener(listener);

    // run suites
    int success = 0,
        failures = 0,
        ignores = 0;
    for (Class suite : suites) {
      sayNothing();
      if (underTC) say("##teamcity[testSuiteStarted name='%s']", suite.getSimpleName());

      Result result = core.run(suite);

      success += result.getRunCount() - (result.getFailureCount() + result.getIgnoreCount());
      failures += result.getFailureCount();
      ignores += result.getIgnoreCount();

      if (underTC) say("##teamcity[testSuiteFinished name='%s']", suite.getSimpleName());
      sayNothing();
    }

    // the end
    System.exit(failures > 0 ? 1 : 0);
  }


  private static void say(String template, Object... params) {
    Object[] values = new Object[params.length];
    for (int i = 0; i < params.length; i++) {
      Object p = params[i];
      if (p instanceof String) {
        String s = (String) p;
        s = s.replace("|", "||")
             .replace("'", "|'")
             .replace("\n", "|n")
             .replace("\r", "|r")
             .replace("[", "|[")
             .replace("]", "|]");
        values[i] = s;
      }
      else {
        values[i] = p;
      }
    }
    String message = String.format(template, values);
    System.out.println(message);
    System.out.flush();
  }

  private static void sayNothing() {
    System.out.println();
    System.out.flush();
  }
}

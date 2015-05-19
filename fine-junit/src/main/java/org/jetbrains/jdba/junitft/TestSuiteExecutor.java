package org.jetbrains.jdba.junitft;

import org.junit.internal.JUnitSystem;
import org.junit.internal.RealSystem;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class TestSuiteExecutor {


  private static final String TEAMCITY_DETECT_VAR_NAME = "TEAMCITY_PROJECT_NAME";



  public static void run(final Class... suites) {

    boolean underTC = System.getenv(TEAMCITY_DETECT_VAR_NAME) != null;

    // prepare junit
    JUnitSystem system = new RealSystem();
    JUnitCore core = new JUnitCore();
    RunListener listener = underTC ? new TeamCityListener() : new TextListener(system);
    core.addListener(listener);

    int success = 0,
        failures = 0,
        ignores = 0;

    // run tests
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
  }



  static void say(String template, Object... params) {
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

  static void sayNothing() {
    System.out.println();
    System.out.flush();
  }

}

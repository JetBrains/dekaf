package org.jetbrains.jdba.junitft;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import static org.jetbrains.jdba.junitft.TestSuiteExecutor.say;


/**
 * Reports test runs to TeamCity using TC service messages.
 * See <a href="http://confluence.jetbrains.com/display/TCD8/Build+Script+Interaction+with+TeamCity#BuildScriptInteractionwithTeamCity-ServiceMessages">Interaction with TeamCity</a> for details.
 */
public class TeamCityListener extends RunListener {

  @Override
  public void testStarted(Description d) throws Exception {
    say("##teamcity[testStarted name='%s' captureStandardOutput='true']", getTestName(d));
  }

  @Override
  public void testFailure(Failure f) throws Exception {
    Description d = f.getDescription();
    say("##teamcity[testFailed name='%s' message='%s' details='%s']",
        getTestName(d),
        f.getMessage(),
        f.getTrace());
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

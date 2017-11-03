package org.jetbrains.dekaf.teamcity

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier


class TeamCityListener : TestExecutionListener {

    override fun executionStarted(testIdentifier: TestIdentifier) {
        TeamCityMessages.reportTestStarted(testIdentifier.displayName)
    }

    override fun executionFinished(testIdentifier: TestIdentifier, testExecutionResult: TestExecutionResult) {
        val status = testExecutionResult.status
        if (status != TestExecutionResult.Status.SUCCESSFUL) {
            TeamCityMessages.reportTestFailure(testIdentifier.displayName, status.name, testExecutionResult.throwable.orElse(null))
        }
        TeamCityMessages.reportTestFinished(testIdentifier.displayName)
    }

    override fun executionSkipped(testIdentifier: TestIdentifier, reason: String?) {
        TeamCityMessages.reportTestIgnored(testIdentifier.displayName)
    }
    
}
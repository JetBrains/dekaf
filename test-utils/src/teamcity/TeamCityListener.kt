package org.jetbrains.dekaf.test.utils.teamcity

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import java.util.*

/**
 *
 */
class TeamCityListener : TestExecutionListener {

    private val displayNames = HashMap<String,String>()


    override fun executionStarted(testIdentifier: TestIdentifier) {
        val parentId = testIdentifier.parentId.value
        val thisDisplayName =
            if (parentId == null || parentId == "[engine:junit-jupiter]") {
                testIdentifier.displayName
            }
            else {
                (displayNames[parentId] ?: parentId) + '.' + testIdentifier.displayName
            }
        displayNames[testIdentifier.uniqueId] = thisDisplayName
        if (testIdentifier.type == TestDescriptor.Type.CONTAINER) return

        TeamCityMessages.reportTestStarted(thisDisplayName)
    }

    override fun executionFinished(testIdentifier: TestIdentifier, testExecutionResult: TestExecutionResult) {
        val status = testExecutionResult.status
        val displayName = displayNames[testIdentifier.uniqueId] ?: testIdentifier.uniqueId
        if (status != TestExecutionResult.Status.SUCCESSFUL) {
            TeamCityMessages.reportTestFailure(displayName, status.name, testExecutionResult.throwable.orElse(null))
        }
        TeamCityMessages.reportTestFinished(displayName)
    }

    override fun executionSkipped(testIdentifier: TestIdentifier, reason: String?) {
        val displayName = displayNames[testIdentifier.uniqueId] ?: testIdentifier.uniqueId
        TeamCityMessages.reportTestIgnored(displayName)
    }


    val <T> Optional<T>.value: T? get() = if (isPresent) get() else null

}

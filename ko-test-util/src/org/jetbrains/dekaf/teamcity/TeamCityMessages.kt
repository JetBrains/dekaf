package org.jetbrains.dekaf.teamcity

import org.jetbrains.dekaf.util.getStackTraceText
import org.opentest4j.AssertionFailedError
import java.nio.file.Path

/**
 * Simple methods for reporting TeamCity service messages.
 *
 * @author Leonid Bushuev from JetBrains
 */
object TeamCityMessages {

    private val output = System.out


    /**
     * If this variable presents in the OS environment,
     * then we can expect that our tests are running under TeamCity.
     */
    private const val teamcityDetectVarName = "TEAMCITY_PROJECT_NAME"

    /**
     * Whether we're under TeamCity.
     */
    val underTeamCity = System.getenv(teamcityDetectVarName) != null


    fun reportSuiteStarted(suiteName: String?) {
        if (suiteName == null) return
        if (underTeamCity)
            say("##teamcity[testSuiteStarted name='%s']", suiteName)
    }

    fun reportSuiteFinished(suiteName: String?) {
        if (suiteName == null) return
        if (underTeamCity)
            say("##teamcity[testSuiteFinished name='%s']", suiteName)
    }

    fun reportTestStarted(testName: String) {
        if (underTeamCity)
            say("##teamcity[testStarted name='%s' captureStandardOutput='true']", testName)
    }

    fun reportPublishFile(file: Path) {
        if (underTeamCity) {
            val p = file.toAbsolutePath().toString()
            say("##teamcity[publishArtifacts '%s']", p)
        }
    }

    fun reportTestFailure(testName: String, messagePrefix: String, exception: Throwable?) {
        if (underTeamCity) {
            if (exception == null) {
                reportTestFailure(testName, messagePrefix, "No exception")
            }
            else if (exception is AssertionFailedError) {
                //System.err.println("CAPTURED ${exception.javaClass.name}")
                val exp = exception.expected.value?.toString() ?: "<null>"
                val act = exception.actual.value?.toString() ?: "<null>"
                val text = exception.getStackTraceText()
                reportDifference(testName, exp, act, text)
            }
            else {
                //System.err.println("MISSED ${exception.javaClass.name}")
                val message = "$messagePrefix: ${exception.javaClass.simpleName}: ${exception.message}"
                val text = exception.getStackTraceText()
                reportTestFailure(testName, message, text)
            }
        }
    }

    private fun reportTestFailure(testName: String, errorMessage: String, stackTrace: CharSequence) {
        if (underTeamCity)
            say("##teamcity[testFailed name='%s' message='%s' details='%s']",
                testName, errorMessage, stackTrace)
    }

    private fun reportDifference(testName: String, expected: CharSequence, actual: CharSequence, text: CharSequence) {
        if (underTeamCity)
            say("##teamcity[testFailed type='comparisonFailure' name='%s' message='Difference' details='%s' expected='%s' actual='%s']",
                testName, text, expected, actual)
    }

    fun reportTestIgnored(testName: String) {
        if (underTeamCity)
            say("##teamcity[testIgnored name='%s'", testName)
    }

    fun reportTestFinished(testName: String) {
        if (underTeamCity)
            say("##teamcity[testFinished name='%s']", testName)
    }




    private fun say(template: String, vararg params: Any) {
        val values = arrayOfNulls<Any>(params.size)
        for (i in params.indices) {
            val p = params[i]
            if (p is String) {
                var s = p
                s = s.replace("|", "||")
                     .replace("'", "|'")
                     .replace("\n", "|n")
                     .replace("\r", "|r")
                     .replace("[", "|[")
                     .replace("]", "|]")
                values[i] = s
            }
            else {
                values[i] = p
            }
        }

        val message = String.format(template, *values)
        output.println(message)
        output.flush()
    }

    fun sayNothing() {
        output.println()
        output.flush()
    }

}
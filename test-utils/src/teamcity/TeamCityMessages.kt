package org.jetbrains.dekaf.test.utils.teamcity

import java.io.StringWriter

/**
 * Simple methods for reporting TeamCity service messages.
 */
object TeamCityMessages {


    var messageConsumer: (String) -> Unit = TeamCityMessages::printConsumer


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

    fun reportTestFailure(testName: String, messagePrefix: String, exception: Throwable?) {
        if (underTeamCity) {
            if (exception != null) {
                val message = "$messagePrefix: ${exception.javaClass.simpleName}: ${exception.message}"
                reportTestFailure(testName, message, exception.getStackTraceText())
            }
            else {
                reportTestFailure(testName, messagePrefix, "No exception")
            }
        }
    }

    fun reportTestFailure(testName: String, errorMessage: String, stackTrace: String) {
        if (underTeamCity)
            say("##teamcity[testFailed name='%s' message='%s' details='%s']",
                testName,
                errorMessage,
                stackTrace)
    }

    fun reportTestIgnored(testName: String) {
        if (underTeamCity)
            say("##teamcity[testIgnored name='%s'", testName)
    }

    fun reportTestFinished(testName: String) {
        if (underTeamCity)
            say("##teamcity[testFinished name='%s']", testName)
    }


    private fun Throwable.getStackTraceText(): String {
        val w = StringWriter(4096)
        this.printStackTrace(java.io.PrintWriter(w))
        return w.buffer.toString();
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
        messageConsumer(message)
    }

    
    fun printConsumer(message: String) {
        println(message)
    }

}
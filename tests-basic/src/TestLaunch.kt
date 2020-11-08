@file:Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")

package org.jetbrains.dekaf.tests.basic

import org.jetbrains.dekaf.test.utils.blame
import org.jetbrains.dekaf.test.utils.halt
import org.jetbrains.dekaf.test.utils.say
import org.jetbrains.dekaf.test.utils.teamcity.TeamCityListener
import org.jetbrains.dekaf.test.utils.teamcity.TeamCityMessages
import org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage
import org.junit.platform.launcher.Launcher
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.TagFilter.includeTags
import org.junit.platform.launcher.TestPlan
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import org.junit.platform.launcher.listeners.TestExecutionSummary
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList


object TestLaunch {

    const val jarsDir = "./jars"
    const val jarsNameSuffix = "-test.jar"


    init {
        TeamCityMessages.messageConsumer = ::say
    }


    @JvmStatic
    fun main(args: Array<String>) {

        if (args.isEmpty()) {
            blame("Specify the tag expression\n")
            return
        }
        if (args.size > 1) {
            blame("Too many arguments.\nSpecify the tag expression as the first argument.\n")
            return
        }

        launchTests(args[0])
    }


    private fun launchTests(tagExpressions: String) {
        say("Looking for the jars...")
        val jarsPath = Path.of(jarsDir)
        if (!jarsPath.exists) halt("Jars directory not found", 0x21)
        val allFiles = Files.list(jarsPath).toList()
        if (allFiles.isEmpty()) halt("Jars directory is empty", 0x22)
        val jarFiles = allFiles.filter { it.fileName.toString().endsWith(".jar", ignoreCase = true) }
        if (jarFiles.isEmpty()) halt("Jars directory contains ${allFiles.size} files but not jars", 0x23)
        val testJarFiles = jarFiles.filter { it.fileName.toString().endsWith(jarsNameSuffix, ignoreCase = true) }
        if (testJarFiles.isEmpty()) halt("Jars directory contains ${allFiles.size} final including ${jarFiles.size} jars but not jars with tests", 0x24)
        say("Found ${testJarFiles.size} jar files:\n" + testJarFiles.joinToString("") { "\t${it.fileName}\n" })

        say("Tag expression: $tagExpressions")
        val request: LauncherDiscoveryRequest = LauncherDiscoveryRequestBuilder.request()
            .selectors(selectPackage("org.jetbrains.dekaf"))
            .filters(includeTags(tagExpressions))
            .build()
        val launcher: Launcher = LauncherFactory.create()
        val testPlan: TestPlan = launcher.discover(request)
        val listener1 = TeamCityListener()
        val listener2 = SummaryGeneratingListener()
        launcher.registerTestExecutionListeners(listener1, listener2)
        launcher.execute(testPlan)
        val summary: TestExecutionSummary = listener2.summary

        val message = "\n" +
                      "========== SUMMARY ==========\n" +
                      "  All found containers:  ${summary.containersFoundCount}\n" +
                      "  Started containers:    ${summary.containersStartedCount}\n" +
                      "  Successful containers: ${summary.containersSucceededCount}\n" +
                      "  Skipped containers:    ${summary.containersSkippedCount}\n" +
                      "  Failed containers:     ${summary.containersFailedCount}\n" +
                      "  Aborted containers:    ${summary.containersAbortedCount}\n" +
                      "-----------------------------\n" +
                      "  All found tests:       ${summary.testsFoundCount}\n" +
                      "  Started tests:         ${summary.testsStartedCount}\n" +
                      "  Successful tests:      ${summary.testsSucceededCount}\n" +
                      "  Skipped tests:         ${summary.testsSkippedCount}\n" +
                      "  Failed tests:          ${summary.testsFailedCount}\n" +
                      "  Aborted tests:         ${summary.testsAbortedCount}\n" +
                      "-----------------------------\n"
        
        say(message)
        Thread.sleep(10L)

        val totalOkCount = summary.testsSucceededCount
        val totalFailureCount = summary.totalFailureCount
        when {
            totalOkCount == 0L      -> {
                val message1 = if (summary.testsFoundCount == 0L) "No tests found." else "All tests failed."
                halt(message1, errorCode = 0x10)
            }
            totalFailureCount == 0L -> {
                say("OK\n")
            }
            else                    -> {
                halt("Total $totalFailureCount failures.", errorCode = 0x02)
            }
        }
    }


    private val Path.exists: Boolean
        get() = Files.exists(this)


}
package org.jetbrains.dekaf


import org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns
import org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener


object AllUnitTests {

    @JvmStatic
    fun main(args: Array<String>) {


        val request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage("org.jetbrains.dekaf"))
                .filters(includeClassNamePatterns(".*Test"))
                .build()

        val launcher = LauncherFactory.create()
        val listener = SummaryGeneratingListener()
        launcher.registerTestExecutionListeners(listener)
        launcher.execute(request, *arrayOfNulls<TestExecutionListener>(0))

        val summary = listener.summary
        val message =
                "Successful tests: ${summary.testsSucceededCount}\n" +
                "Skipped tests:    ${summary.testsSkippedCount}\n" +
                "Failed tests:     ${summary.testsFailedCount}\n" +
                "Aborted tests:    ${summary.testsAbortedCount}\n" 
        System.out.println(message)
        System.out.flush()
        Thread.sleep(100L)

        val totalFailureCount = summary.totalFailureCount
        if (totalFailureCount > 0) {
            System.err.println("Total $totalFailureCount failures.")
            System.exit(-1)
        }
    }


}

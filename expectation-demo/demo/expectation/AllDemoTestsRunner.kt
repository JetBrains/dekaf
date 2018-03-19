package org.jetbrains.dekaf.expectation

import org.junit.platform.engine.discovery.ClassNameFilter
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.launcher.TagFilter
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.LoggingListener
import java.io.PrintWriter
import java.io.StringWriter
import java.util.function.Supplier


object AllDemoTestsRunner {


    val showStackTrace = true


    @JvmStatic
    fun main(args: Array<String>) {

        val request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectPackage("org.jetbrains.dekaf"))
                .filters(ClassNameFilter.includeClassNamePatterns(".*Test"),
                         TagFilter.includeTags("ExpectationDemoTest"))
                .build()


        val listener = LoggingListener.forBiConsumer { exception: Throwable?, messageSupplier: Supplier<String?> ->
            val message: String? = messageSupplier.get()
            when {
                message != null && exception == null -> {
                    System.out.println(message)
                }
                message == null && exception != null -> {
                    val b = StringBuilder()
                    b.report(exception)
                    System.out.println(b)
                }
                message != null && exception != null -> {
                    val b = StringBuilder()
                    val exceptionMessage= exception.message

                    if (exceptionMessage == null) {
                        b.append(message).ensureEoln()
                    }
                    else if (exceptionMessage !in message) {
                        if (exceptionMessage.indexOf(message) !in 0..100) b.append(message).ensureEoln()
                    }
                    
                    b.report(exception)
                    System.out.println(b)
                }
            }
        }

        val launcher = LauncherFactory.create()
        launcher.registerTestExecutionListeners(listener)
        launcher.execute(request, *arrayOfNulls<TestExecutionListener>(0))

        System.out.flush()
    }


    private fun StringBuilder.report(exception: Throwable): StringBuilder {
        when (showStackTrace) {
            true  -> this.appendException(exception)
            false -> this.append(exception.message ?: exception.javaClass.simpleName)
        }
        return this
    }

    private fun StringBuilder.appendException(exception: Throwable): StringBuilder {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        exception.printStackTrace(pw)
        this.append(sw.buffer)
        return this
    }

    private fun StringBuilder.ensureEoln(): StringBuilder {
        val n = this.length
        val ok = n > 0 && this[n-1] == '\n'
        if (!ok) append('\n')
        return this
    }

}
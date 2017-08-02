package org.jetbrains.dekaf.testing

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock


object TerminalListener : TestExecutionListener {

    private val testEntries = ArrayList<TestEntry>()
    private val testEntriesByIdentifier: MutableMap<String,TestEntry> = HashMap()
    private val testCounter = AtomicInteger(0)

    private val sync = ReentrantLock()
    private var wasErr = false


    private fun createOrGetTestEntry(identifier: TestIdentifier): TestEntry {
        val id = identifier.uniqueId
        var entry = testEntriesByIdentifier[id]
        if (entry == null) {
            entry = TestEntry(testCounter.incrementAndGet(), identifier)
            testEntries += entry
            testEntriesByIdentifier[id] = entry
        }
        return entry
    }

    override fun executionStarted(testIdentifier: TestIdentifier) {
        val entry = createOrGetTestEntry(testIdentifier)
    }

    override fun executionFinished(testIdentifier: TestIdentifier, testExecutionResult: TestExecutionResult) {
        val entry = createOrGetTestEntry(testIdentifier)
        entry.result = testExecutionResult
    }

    override fun executionSkipped(testIdentifier: TestIdentifier, reason: String?) {
        val entry = createOrGetTestEntry(testIdentifier)
        entry.skipReason = reason
    }


    fun displayAll() {
        var skippedTests = 0
        for (entry in testEntries) {
            val result = entry.result ?: continue
            when (result.status) {
                TestExecutionResult.Status.FAILED -> printFail(entry)
                TestExecutionResult.Status.ABORTED -> printFail(entry)
            }
        }
    }

    private fun printFail(entry: TestEntry) {
        val identifier = entry.identifier
        val word1 =
                when {
                    identifier.isContainer -> "Container"
                    identifier.isTest      -> "Test"
                    else                   -> "Something"
                }
        var name = identifier.legacyReportingName
        var p: String? = identifier.parentId.orElse(null)
        while (p != null) {
            val parent = testEntriesByIdentifier[p]
            if (parent == null || !parent.identifier.source.isPresent) break
            p = parent.identifier.parentId?.orElse(null)
            name = parent.identifier.legacyReportingName + '.' + name
        }

        out("-------- $word1 $name --------")
        val exception: Throwable? = entry.result?.throwable?.orElse(null)
        if (exception != null) {
            //out(exception.message)
            exception.printStackTrace(System.out)
        }
        out("")
    }


    private fun out(text: String?) {
        if (text == null) return
        synchronized(sync) {
            if (wasErr) {
                System.err.flush()
                wasErr = false
            }

            System.out.println(text)
        }
    }

    private fun err(text: String?) {
        if (text == null) return
        synchronized(sync) {
            if (!wasErr) {
                System.out.flush()
                wasErr = true
            }

            System.out.println(text)
        }
    }

}
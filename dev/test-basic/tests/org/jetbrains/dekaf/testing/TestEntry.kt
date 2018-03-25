package org.jetbrains.dekaf.testing

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.launcher.TestIdentifier


data class TestEntry (
        val order: Int,
        val identifier: TestIdentifier,
        var result: TestExecutionResult? = null,
        var skipReason: String? = null
)

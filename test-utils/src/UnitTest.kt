package org.jetbrains.dekaf.test.utils


import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestInstance


@Tag("UnitTest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
interface UnitTest

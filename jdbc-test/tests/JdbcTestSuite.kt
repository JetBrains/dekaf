package org.jetbrains.dekaf.jdbc.test

import org.jetbrains.dekaf.jdbc.test.drivers.JdbcDriverLoaderTest
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite


@Suite
@SelectClasses(
        JdbcDirectoryTest::class,
        JdbcDriverLoaderTest::class,
)
class JdbcTestSuite 
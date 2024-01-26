package org.jetbrains.dekaf.jdbc.test

import org.jetbrains.dekaf.jdbc.test.drivers.H2DatabaseConnectionsTest
import org.jetbrains.dekaf.jdbc.test.drivers.H2DriverTest
import org.jetbrains.dekaf.jdbc.test.drivers.JdbcDriverLoaderTest
import org.jetbrains.dekaf.jdbc.test.testFixtures.JdbcDirectoryTest
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite


@Suite
@SelectClasses(
        JdbcDirectoryTest::class,
        JdbcDriverLoaderTest::class,
        H2DriverTest::class,
        H2DatabaseConnectionsTest::class,
)
class JdbcTestSuite 
package org.jetbrains.dekaf.jdbc.test.drivers

import lb.yaka.base.expectations.equalsTo
import lb.yaka.base.expectations.sameAs
import lb.yaka.base.gears.expect
import org.jetbrains.dekaf.inter.test.utils.UnitTest
import org.jetbrains.dekaf.jdbc.drivers.JdbcDriver
import org.jetbrains.dekaf.jdbc.drivers.JdbcDriverLoader
import org.jetbrains.dekaf.jdbc.test.DriverFiles
import org.junit.jupiter.api.Test
import java.nio.file.Path

class JdbcDriverLoaderTest : UnitTest {

    @Test
    fun loadDriverForH2() {
        val driversPath = Path.of("jdbc-drivers", "h2")
        val driver: JdbcDriver = JdbcDriverLoader.loadDriver(driversPath, arrayOf(DriverFiles.jarNameForH2), DriverFiles.driverClassNameForH2)
        driver.use { driver ->
            expect that driver.driverClass.name equalsTo DriverFiles.driverClassNameForH2
            expect that driver.driverClass.classLoader sameAs driver.driverClassLoader
        }
    }

    @Test
    fun loadDriverForPostgres() {
        val driversPath = Path.of("jdbc-drivers", "postgres")
        val driver: JdbcDriver = JdbcDriverLoader.loadDriver(driversPath, arrayOf(DriverFiles.jarNameForPostgres), DriverFiles.driverClassNameForPostgres)
        driver.use { driver ->
            expect that driver.driverClass.name equalsTo DriverFiles.driverClassNameForPostgres
            expect that driver.driverClass.classLoader sameAs driver.driverClassLoader
        }
    }

}
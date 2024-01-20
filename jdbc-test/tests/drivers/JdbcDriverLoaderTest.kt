package org.jetbrains.dekaf.jdbc.test.drivers

import lb.yaka.base.expectations.equalsTo
import lb.yaka.base.expectations.iz
import lb.yaka.base.expectations.sameAs
import lb.yaka.base.gears.aka
import lb.yaka.base.gears.expect
import org.jetbrains.dekaf.inter.test.utils.SystemTest
import org.jetbrains.dekaf.jdbc.drivers.JdbcDriver
import org.jetbrains.dekaf.jdbc.drivers.JdbcDriverLoader
import org.jetbrains.dekaf.jdbc.test.DriverFiles
import org.junit.jupiter.api.*
import java.nio.file.Path
import java.util.*


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class JdbcDriverLoaderTest : SystemTest {

    private val noProperties = Properties()

    @Test @Order(1)
    fun loadDriverForH2_byClassName() {
        val driversPath = Path.of("jdbc-drivers", "h2")
        val driver: JdbcDriver = JdbcDriverLoader.loadDriver(driversPath, arrayOf(DriverFiles.jarNameForH2), DriverFiles.driverClassNameForH2)
        driver.use { driver ->
            expect that driver.driver.javaClass.name equalsTo DriverFiles.driverClassNameForH2
            expect that driver.driver.javaClass.classLoader sameAs driver.driverClassLoader
        }
    }

    @Test @Order(2)
    fun loadDriverForH2_usingService() {
        val driversPath = Path.of("jdbc-drivers", "h2")
        val driver: JdbcDriver = JdbcDriverLoader.loadDriver(driversPath, arrayOf(DriverFiles.jarNameForH2), null)
        driver.use { driver ->
            expect that driver.driver.javaClass.name equalsTo DriverFiles.driverClassNameForH2
            expect that driver.driver.javaClass.classLoader sameAs driver.driverClassLoader
        }
    }

    @Test @Order(3)
    fun loadDriverForPostgres_byClassName() {
        val driversPath = Path.of("jdbc-drivers", "postgres")
        val driver: JdbcDriver = JdbcDriverLoader.loadDriver(driversPath, arrayOf(DriverFiles.jarNameForPostgres), DriverFiles.driverClassNameForPostgres)
        driver.use { driver ->
            expect that driver.driver.javaClass.name equalsTo DriverFiles.driverClassNameForPostgres
            expect that driver.driver.javaClass.classLoader sameAs driver.driverClassLoader
        }
    }

    @Test @Order(4)
    fun loadDriverForPostgres_usingService() {
        val driversPath = Path.of("jdbc-drivers", "postgres")
        val driver: JdbcDriver = JdbcDriverLoader.loadDriver(driversPath, arrayOf(DriverFiles.jarNameForPostgres), null)
        driver.use { driver ->
            expect that driver.driver.javaClass.name equalsTo DriverFiles.driverClassNameForPostgres
            expect that driver.driver.javaClass.classLoader sameAs driver.driverClassLoader
        }
    }

    @Test @Order(5)
    fun canConnect() {
        val driversPath = Path.of("jdbc-drivers", "h2")
        val driver: JdbcDriver = JdbcDriverLoader.loadDriver(driversPath, arrayOf(DriverFiles.jarNameForH2), DriverFiles.driverClassNameForH2)
        driver.use { driver ->
            val connection = driver.driver.connect("jdbc:h2:mem:Test", noProperties)
            connection.use { conn ->
                conn.isClosed aka "Connection is closed" iz false
            }
            connection.isClosed aka "Connection is closed" iz true
        }
    }


    @AfterAll
    fun freeMemory() {
        System.gc()
    }

}
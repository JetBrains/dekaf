package org.jetbrains.dekaf.jdbc.test.testFixtures

import org.jetbrains.dekaf.jdbc.drivers.JdbcDriver
import org.jetbrains.dekaf.jdbc.drivers.JdbcDriverLoader
import org.jetbrains.dekaf.jdbc.test.DriverFiles
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.nio.file.Path


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class H2DriverTestCase {

    protected companion object {

        lateinit var driver: JdbcDriver

        @BeforeAll @JvmStatic
        fun loadDriver() {
            val driverPath = Path.of("jdbc-drivers", "h2")
            driver = JdbcDriverLoader.loadDriver(driverPath, arrayOf(DriverFiles.jarNameForH2), DriverFiles.driverClassNameForH2)
            assert(driver != null)
        }


        @AfterAll @JvmStatic
        fun unloadDriver() {
            driver.close()
            System.gc()
        }
        
    }

}
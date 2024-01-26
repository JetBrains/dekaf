package org.jetbrains.dekaf.jdbc.test.drivers

import org.jetbrains.dekaf.jdbc.test.DriverFiles
import org.jetbrains.dekaf.jdbc.test.testFixtures.H2DriverTestCase
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.sql.Connection
import java.util.*


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class H2DriverTest : H2DriverTestCase() {

    @Test @Order(1)
    fun connect_disconnect() {
        val connection: Connection =
                driver.driver.connect(DriverFiles.testConnectionForH2, Properties())
        connection.isValid(1)
        connection.close()
    }


    @Test @Order(2)
    fun statement1() {
        driver.driver.connect(DriverFiles.testConnectionForH2, Properties()).use { connection: Connection ->
            val statement = connection.prepareStatement("commit")
            statement.execute()
            statement.close()
        }
    }

}
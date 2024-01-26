package org.jetbrains.dekaf.jdbc.test.testFixtures

import org.jetbrains.dekaf.jdbc.test.DriverFiles
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import java.sql.Connection
import java.util.*

abstract class H2DatabaseConnectionTestCase : H2DriverTestCase() {

    protected companion object {

        lateinit var connection: Connection

        internal val connectionProperties = Properties()

        @BeforeAll @JvmStatic
        fun databaseInit() {
            connection = driver.driver.connect(DriverFiles.testConnectionForH2, connectionProperties)
        }

        @AfterAll @JvmStatic
        fun databaseShutdown() {
            connection.rollback()
            connection.perform("shutdown immediately")
            connection.close()
        }

    }

    @BeforeEach
    fun careConnection() {
        connection.autoCommit = false
    }

    @AfterEach
    fun rollbackConnection() {
        connection.rollback()
    }

}
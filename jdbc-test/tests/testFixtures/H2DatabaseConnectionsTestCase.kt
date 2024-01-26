package org.jetbrains.dekaf.jdbc.test.testFixtures

import org.jetbrains.dekaf.jdbc.test.DriverFiles
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import java.sql.Connection

abstract class H2DatabaseConnectionsTestCase : H2DatabaseConnectionTestCase() {

    protected companion object {

        lateinit var connection1: Connection
        lateinit var connection2: Connection

        @BeforeAll @JvmStatic
        fun connectionsOpen() {
            connection1 = driver.driver.connect(DriverFiles.testConnectionForH2, connectionProperties)
            connection2 = driver.driver.connect(DriverFiles.testConnectionForH2, connectionProperties)
        }

        @AfterAll @JvmStatic
        fun connectionsClose() {
            connection2.rollback()
            connection1.rollback()
            connection2.close()
            connection1.close()
        }

    }

    @BeforeEach
    fun careConnections() {
        connection1.autoCommit = false
        connection2.autoCommit = false
    }

    @AfterEach
    fun rollbackConnections() {
        connection2.rollback()
        connection1.rollback()
    }

}
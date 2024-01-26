package org.jetbrains.dekaf.jdbc.test.drivers

import org.jetbrains.dekaf.jdbc.test.testFixtures.H2DatabaseConnectionsTestCase
import org.jetbrains.dekaf.jdbc.test.testFixtures.perform
import org.junit.jupiter.api.Test

class H2DatabaseConnectionsTest : H2DatabaseConnectionsTestCase() {

    @Test
    fun `check that both connections share the same database`() {
        connection.perform("create view View_that_is_shared_between_connections as select 26")
        connection1.perform("select 1 from View_that_is_shared_between_connections")
        connection2.perform("select 2 from View_that_is_shared_between_connections")
    }

}
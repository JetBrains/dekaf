@file:JvmName("JdbcTestUtils")

package org.jetbrains.dekaf.jdbc.test.testFixtures

import java.sql.Connection


fun Connection.perform(command: String) {
    val stmt = this.createStatement()
    stmt.use { stmt ->
        stmt.execute(command)
    }
}

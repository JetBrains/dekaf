package org.jetbrains.dekaf.mainTest.base

import org.jetbrains.dekaf.main.db.inSession
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test


class BaseSessionTest : UnitTestWithH2 {

    @Test @Order(1)
    fun basic_ping() {
        dbf.inSession { session ->
            session.ping()
        }
    }

}

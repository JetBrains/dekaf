package org.jetbrains.dekaf.mainTest.base

import org.jetbrains.dekaf.main.db.inSession
import org.jetbrains.dekaf.main.db.inTransaction
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test


class BaseSessionTest : UnitTestWithH2 {

    @Test @Order(1)
    fun basic_ping() {
        dbf.inSession { session ->
            session.ping()
        }
    }

    @Test @Order(2)
    fun basic_perform_beyondTransaction() {
        dbf.inSession { session ->
            session.perform("call 2*2")
        }
    }

    @Test @Order(3)
    fun basic_perform_inTransaction() {
        dbf.inTransaction { transaction ->
            transaction.perform("call 2*2")
        }
    }

}

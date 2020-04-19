package org.jetbrains.dekaf.mainTest.base

import lb.yaka.expectations.equalsTo
import lb.yaka.expectations.iz
import lb.yaka.expectations.zero
import lb.yaka.gears.expect
import org.jetbrains.dekaf.main.db.inSession
import org.jetbrains.dekaf.test.utils.performMass
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test


class BaseFacadeTest : UnitTestWithH2 {

    @Test @Order(1)
    fun basic_session_open_ping_close() {
        expect that dbf.countActiveSessions() iz zero

        val session = dbf.openSession()
        session.ping()

        expect that dbf.countActiveSessions() equalsTo 1

        session.close()

        expect that dbf.countActiveSessions() iz zero
    }


    @Test @Order(31)
    fun mass_session_open_ping_close() {
        expect that dbf.countActiveSessions() iz zero

        performMass(10, 10) { _, _ ->
            dbf.inSession { session ->
                session.ping()
            }
        }

        expect that dbf.countActiveSessions() iz zero
    }

}

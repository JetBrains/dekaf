package org.jetbrains.dekaf.mainTest

import lb.yaka.expectations.equalsTo
import lb.yaka.expectations.iz
import lb.yaka.expectations.zero
import lb.yaka.gears.expect
import org.jetbrains.dekaf.main.DbMaster
import org.jetbrains.dekaf.mainTest.util.H2memSettings
import org.jetbrains.dekaf.test.utils.UnitTest
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class DbMasterTest : UnitTest {

    @Test @Order(1)
    fun open() {
        val facade = DbMaster.open(H2memSettings)
        expect that facade.countActiveSessions() iz zero
        expect that facade.isConnected() equalsTo false
    }

    @Test @Order(1)
    fun connect_ping() {
        val facade = DbMaster.open(H2memSettings)

        facade.connect()

        expect that facade.isConnected() equalsTo true
        expect that facade.isConnected(true) equalsTo true

        facade.disconnect()

        expect that facade.isConnected() equalsTo false
    }

}
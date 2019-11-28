package org.jetbrains.dekaf.jdbcTest.impl

import lb.yaka.expectations.equalsTo
import lb.yaka.expectations.iz
import lb.yaka.expectations.zero
import lb.yaka.gears.expect
import org.jetbrains.dekaf.inter.settings.Settings
import org.jetbrains.dekaf.jdbc.impl.JdbcFacade
import org.jetbrains.dekaf.test.utils.UnitTest
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class JdbcFacadeTest : UnitTest {

    companion object {
        val memH2Settings =
                Settings.of(
                        "driver", Settings.of("class", "org.h2.Driver"),
                        "jdbc", Settings.of("connection-string", "jdbc:h2:mem:test")
                )
    }

    @Test @Order(1)
    fun init_basic() {
        val facade = JdbcFacade()
        facade.init(memH2Settings)
        expect that facade.sessionsCount iz zero
    }


    @Test @Order(2)
    fun connectAndDisconnect() {
        val facade = JdbcFacade()
        facade.init(memH2Settings)
        val session = facade.openSession()
        expect that facade.sessionsCount equalsTo 1
        expect that session.isClosed equalsTo false
        session.ping()
        session.close()
        expect that facade.sessionsCount iz zero
        expect that session.isClosed equalsTo true
    }




}
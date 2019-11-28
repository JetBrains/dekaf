package org.jetbrains.dekaf.jdbcTest.impl

import org.jetbrains.dekaf.inter.settings.Settings
import org.jetbrains.dekaf.jdbc.impl.JdbcFacade
import org.jetbrains.dekaf.jdbc.impl.JdbcSession
import org.jetbrains.dekaf.test.utils.UnitTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
abstract class H2ConnectedTest : UnitTest {

    companion object {
        val memH2Settings =
                Settings.of(
                        "driver", Settings.of("class", "org.h2.Driver"),
                        "jdbc", Settings.of("connection-string", "jdbc:h2:mem:test")
                )
    }

    protected lateinit var facade:  JdbcFacade
    protected lateinit var session: JdbcSession

    @BeforeAll
    fun openH2() {
        facade = JdbcFacade()
        facade.init(memH2Settings)
        session = facade.openSession()
    }

    @AfterAll
    fun closeH2() {
        facade.closeAllSessions()
    }




}
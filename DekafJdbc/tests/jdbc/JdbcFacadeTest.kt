package org.jetbrains.dekaf.jdbc;



import org.jetbrains.dekaf.assertions.IsFalse
import org.jetbrains.dekaf.assertions.IsTrue
import org.jetbrains.dekaf.assertions.expected
import org.jetbrains.dekaf.jdbc.H2mem.hmFacade
import org.junit.jupiter.api.Test


class JdbcFacadeTest {

    @Test
    fun activate() {
        hmFacade.activate()
        hmFacade.isActive expected IsTrue
        hmFacade.deactivate()
        hmFacade.isActive expected IsFalse
    }

    @Test
    fun obtainConnection() {
        hmFacade.activate()
        val connection = hmFacade.obtainConnection()
        connection.isClosed expected IsFalse
        connection.close()
    }

    @Test
    fun openSession_closeSession() {
        hmFacade.activate()

        hmFacade.countSessions() expected 0

        val session1 = hmFacade.openSession()
        val session2 = hmFacade.openSession()

        hmFacade.countSessions() expected 2

        session1.close()
        session2.close()

        hmFacade.countSessions() expected 0
    }

    @Test
    fun openSession_deactivate() {
        hmFacade.activate()

        hmFacade.countSessions() expected 0

        hmFacade.openSession()
        hmFacade.openSession()
        hmFacade.openSession()

        hmFacade.countSessions() expected 3

        hmFacade.deactivate()

        hmFacade.countSessions() expected 0
    }


}
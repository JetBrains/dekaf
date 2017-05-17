package org.jetbrains.dekaf.jdbc;



import org.jetbrains.dekaf.assertions.IsFalse
import org.jetbrains.dekaf.assertions.IsTrue
import org.jetbrains.dekaf.assertions.expected
import org.jetbrains.dekaf.jdbc.H2mem.h2facade
import org.junit.jupiter.api.Test


class JdbcFacadeTest {

    @Test
    fun activate() {
        h2facade.activate()
        h2facade.isActive expected IsTrue
        h2facade.deactivate()
        h2facade.isActive expected IsFalse
    }

    @Test
    fun obtainConnection() {
        h2facade.activate()
        val connection = h2facade.obtainConnection()
        connection.isClosed expected IsFalse
        connection.close()
    }

    @Test
    fun openSession_closeSession() {
        h2facade.activate()

        h2facade.countSessions() expected 0

        val session1 = h2facade.openSession(null,null,null)
        val session2 = h2facade.openSession(null,null,null)

        h2facade.countSessions() expected 2

        session1.close()
        session2.close()

        h2facade.countSessions() expected 0
    }

    @Test
    fun openSession_deactivate() {
        h2facade.activate()

        h2facade.countSessions() expected 0

        h2facade.openSession(null,null,null)
        h2facade.openSession(null,null,null)
        h2facade.openSession(null,null,null)

        h2facade.countSessions() expected 3

        h2facade.deactivate()

        h2facade.countSessions() expected 0
    }


}
package org.jetbrains.dekaf.jdbc

import org.jetbrains.dekaf.H2db


internal object H2mem {

    @JvmStatic
    val provider = JdbcProvider()

    @JvmStatic
    val hmFacade = JdbcFacade(provider, H2db.RDBMS, SpecificForH2db())

    init {
        hmFacade.setConnectionString("jdbc:h2:mem:TestDatabase")
    }


    @JvmStatic
    fun hmPerformCommand(command: String) {
        val connection = hmFacade.obtainConnection()
        connection.use { connection ->
            val stmt = connection.createStatement()
            stmt.use { stmt ->
                stmt.execute(command);
            }
        }
    }


    @JvmStatic
    fun<R> hmInSession(block: (JdbcSession) -> R): R {
        hmFacade.activate()
        val session = hmFacade.openSession()
        try {
            return block(session)
        }
        finally {
            session.close()
        }
    }

    @JvmStatic
    fun hmInSessionDo(block: (JdbcSession) -> Unit) {
        hmFacade.activate()
        val session = hmFacade.openSession()
        try {
            block(session)
        }
        finally {
            session.close()
        }
    }


}
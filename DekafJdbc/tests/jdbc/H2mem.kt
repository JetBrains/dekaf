package org.jetbrains.dekaf.jdbc

import org.jetbrains.dekaf.H2db


internal object H2mem {

    val provider = JdbcProvider()
    val hmFacade = JdbcFacade(provider, H2db.RDBMS, SpecificForH2db())

    init {
        hmFacade.setConnectionString("jdbc:h2:mem:TestDatabase")
    }


    fun hmPerformCommand(command: String) {
        val connection = hmFacade.obtainConnection()
        connection.use { connection ->
            val stmt = connection.createStatement()
            stmt.use { stmt ->
                stmt.execute(command);
            }
        }
    }


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
package org.jetbrains.dekaf.core

import java.sql.Connection
import java.sql.SQLException


/**
 * MS SQL Server test helper.
 *
 * @author Leonid Bushuev
 */
class MssqlTestHelper(db: DBFacade) : BaseTestHelper(db, MssqlTestHelper::class) {

    init {
        schemasNotToZap.add("sys")
    }


    override fun prepareX1() {
        val x1 = scriptum.getText("X1")
        db.inSessionDo { session ->
            val conn = session.getSpecificService(Connection::class.java,
                                                  ImplementationAccessibleService.Names.JDBC_CONNECTION)!!
            try {
                conn.autoCommit = true
                val stmt = conn.createStatement()
                stmt.execute(x1.text)
                stmt.close()
            }
            catch (e: SQLException) {
                throw RuntimeException(e)
            }
        }
    }


    override fun prepareX1000() {
        performCommand(scriptum, "X10")
        performCommand(scriptum, "X1000")
    }

    override fun prepareX1000000() {
        prepareX1000()
        performCommand(scriptum, "X1000000")
    }


}

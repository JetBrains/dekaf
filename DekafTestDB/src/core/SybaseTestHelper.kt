package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.sql.SqlQuery
import org.jetbrains.dekaf.text.Scriptum

import org.jetbrains.dekaf.core.layoutExistence


/**
 * Sybase test helper.
 *
 * @author Leonid Bushuev
 */
class SybaseTestHelper(db: DBFacade) : BaseTestHelper(db, SybaseTestHelper::class) {


    private val myTableOrViewExistenceQuery = scriptum.query("TableOrViewExistence", layoutExistence())


    private fun tableExists(name: String): Boolean {
        return db.inSession<Boolean> { session ->
            session.query(myTableOrViewExistenceQuery).withParams(name).run()
        }
    }


    override fun prepareX1() {
        if (tableExists("X1")) return
        performScript(scriptum, "X1")
    }


    fun prepareX10() {
        if (tableExists("X10")) return
        performScript(scriptum, "X10")
    }


    override fun prepareX1000() {
        if (tableExists("X1000")) return
        prepareX10()
        performScript(scriptum, "X1000")
    }

    override fun prepareX1000000() {
        if (tableExists("X1000000")) return
        prepareX1000()
        performCommand(scriptum, "X1000000")
    }

    override fun ensureNoTableOrView(vararg names: String) {
        // Unfortunately, Sybase provides no way to easy drop tables.
        // We have to drop foreign keys first.
        performMetaQueryCommands(scriptum, "EnsureNoForeignKeysMetaQuery", *names)
        performMetaQueryCommands(scriptum, "EnsureNoTableOrViewMetaQuery", *names)
    }

    override fun zapSchemaInternally(connectionInfo: ConnectionInfo) {
        // Unfortunately, Sybase provides no way to easy drop tables.
        // We have to drop foreign keys first.
        performMetaQueryCommands(scriptum, "ZapForeignKeysMetaQuery")
        performMetaQueryCommands(scriptum, "ZapSchemaMetaQuery")
    }
}

package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.text.Scriptum


/**
 * PostgreSQL test helper.
 *
 * @author Leonid Bushuev
 */
class PostgresTestHelper(db: DBFacade) : BaseTestHelper(db, PostgresTestHelper::class) {

    init {
        schemasNotToZap.addAll(arrayOf("pg_catalog","information_schema","sys"))
    }

    override fun prepareX1() {
        performCommand("create or replace view X1 as select 1")
    }

    override fun prepareX1000() {
        performCommand(scriptum, "X1000")
    }

    override fun prepareX1000000() {
        performCommand(scriptum, "X1000000")
    }

    override fun zapSchemaInternally(connectionInfo: ConnectionInfo) {
        val info = db.connectionInfo
        if (info.serverVersion.isOrGreater(9, 1)) performMetaQueryCommands(scriptum, "ZapExtensionsMetaQuery")
        performMetaQueryCommands(scriptum, "ZapSchemaMetaQuery")
    }
}

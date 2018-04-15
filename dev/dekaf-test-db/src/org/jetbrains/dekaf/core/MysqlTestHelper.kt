package org.jetbrains.dekaf.core



/**
 * MySQL test helper.
 * 
 * @author Leonid Bushuev
 */
class MysqlTestHelper(db: DBFacade) : BaseTestHelper(db, MysqlTestHelper::class) {

    init {
        schemasNotToZap.add("performance_schema")
        schemasNotToZap.add("mysql")
    }


    override fun prepareX1() {
        performCommand("create or replace view X1 as select 1")
    }

    override fun prepareX1000() {
        performCommand(scriptum, "X10")
        performCommand(scriptum, "X1000")
    }

    override fun prepareX1000000() {
        prepareX1000()
        performCommand(scriptum, "X1000000")
    }


    override fun ensureNoTableOrView(vararg names: String) {
        // Unfortunately, MySQL provides no way to easy drop tables.
        // We have to drop foreign keys first.
        performMetaQueryCommands(scriptum, "EnsureNoForeignKeysMetaQuery", *names)
        performMetaQueryCommands(scriptum, "EnsureNoTableOrViewMetaQuery", *names)
    }

    override fun zapSchemaInternally(connectionInfo: ConnectionInfo) {
        // Unfortunately, MySQL provides no way to easy drop tables.
        // We have to drop foreign keys first.
        performMetaQueryCommands(scriptum, "ZapForeignKeysMetaQuery")
        performMetaQueryCommands(scriptum, "ZapSchemaMetaQuery")
    }

}

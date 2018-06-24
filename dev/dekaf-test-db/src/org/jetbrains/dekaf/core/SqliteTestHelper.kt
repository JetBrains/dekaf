package org.jetbrains.dekaf.core


/**
 * SQLite test helper.
 */
class SqliteTestHelper(db: DBFacade) : BaseTestHelper(db, SqliteTestHelper::class) {

    override fun prepareX1() {
        performCommand("create view X1 as select 1")
    }

    override fun prepareX1000() {
        performCommand(scriptum, "X1000")
    }

    override fun prepareX1000000() {
        prepareX1000()
        performCommand(scriptum, "X1000000")
    }
}

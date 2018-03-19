package org.jetbrains.dekaf.core


/**
 * H2db test helper.
 *
 * @author Leonid Bushuev
 */
class H2dbTestHelper(db: DBFacade) : BaseTestHelper(db, H2dbTestHelper::class) {

    override fun prepareX1() {
        performCommand("create or replace view X1 as select 1 as X from dual")
    }

    override fun prepareX1000() {
        performCommand(scriptum, "X1000")
    }

    override fun prepareX1000000() {
        prepareX1000()
        performCommand(scriptum, "X1000000")
    }

}

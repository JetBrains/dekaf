package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.text.Scriptum

import java.util.Arrays


/**
 * Oracle test helper.
 * 
 * @author Leonid Bushuev
 */
class OracleTestHelper(db: DBFacade) : BaseTestHelper(db, OracleTestHelper::class) {

    init {
        schemasNotToZap.clear()
        schemasNotToZap.addAll(arrayOf("SYS", "SYSTEM", "SYSMAN", "PUBLIC", "CTXSYS", "DBSNMP",
                                       "APPQOSSYS", "EXFSYS", "ORACLE_OCM", "OUTLN", "WMSYS",
                                       "XDB", "BENCHMARK"))
    }

    override fun fromSingleRowTable(): String {
        return " from dual"
    }

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

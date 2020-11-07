package org.jetbrains.dekaf.mainTest.base

import org.jetbrains.dekaf.main.db.inSession
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test


class BaseTransactionTest : UnitTestWithH2 {

    @Test @Order(1)
    fun basic_commit() {
        dbf.inSession { session ->

            val tran = session.beginTransaction()
            tran.commit()
            tran.close()

        }
    }

}

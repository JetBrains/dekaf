package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.CommonIntegrationCase
import org.jetbrains.dekaf.assertions.expectedException
import org.jetbrains.dekaf.exceptions.NoTableOrViewException
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test


/**
 * @author Leonid Bushuev from JetBrains
 */
class CommonExceptionRecognizingTest : CommonIntegrationCase() {

    @BeforeAll
    @Throws(Exception::class)
    fun setUp() {
        DB.connect()
    }

    @Test
    fun recognize_NoTableOrView() {
        val queryText = "select * from unexistent_table"
        expectedException<NoTableOrViewException> {
            DB.inTransactionDo { tran -> tran.query(queryText, layoutExistence()).run() }
        }
    }

}

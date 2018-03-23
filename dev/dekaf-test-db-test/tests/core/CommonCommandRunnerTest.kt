package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.CommonIntegrationCase
import org.jetbrains.dekaf.sql.SqlCommand
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test


/**
 * @author Leonid Bushuev
 */
@Tag("UnitTest")
class CommonCommandRunnerTest : CommonIntegrationCase() {

    companion object {

        @BeforeAll @JvmStatic
        fun setUp() {
            DB.connect()
        }
        
    }


    @Test
    fun basic_create_drop() {
        TH.ensureNoTableOrView("Tab1")

        val command1 = SqlCommand("create table Tab1 (ColA char(1))")
        val command2 = SqlCommand("drop table Tab1")

        DB.inSessionDo { session ->

            session.command(command1).run()
            session.command(command2).run()

        }
    }


}

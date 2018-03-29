package org.jetbrains.dekaf.core

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.dekaf.CommonIntegrationCase
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.sql.DatabaseMetaData
import java.sql.SQLException


/**
 * @author Leonid Bushuev from JetBrains
 */
@Tag("basic")
abstract class CommonSessionTest : CommonIntegrationCase() {

    companion object {

        @BeforeAll @JvmStatic
        fun setUp() {
            DB.connect()
        }

    }


    @Test
    fun transaction_commit() {
        TH.ensureNoTableOrView("Tab_1")
        TH.performCommand("create table Tab_1 (C1 char(1))")
        TH.performCommand("insert into Tab_1 values ('A')")

        val session = DB.leaseSession()
        try {
            assertThat(TH.countTableRows(session, "Tab_1")).isEqualTo(1)

            session.beginTransaction()

            session.command("insert into Tab_1 values ('B')").run()

            assertThat(TH.countTableRows(session, "Tab_1")).isEqualTo(2)

            session.commit()

            assertThat(TH.countTableRows(session, "Tab_1")).isEqualTo(2)
        }
        finally {
            session.close()
        }
    }


    @Test
    fun transaction_rollback() {
        TH.ensureNoTableOrView("Tab_1")
        TH.performCommand("create table Tab_1 (C1 char(1) not null)")
        TH.performCommand("insert into Tab_1 values ('A')")

        val session = DB.leaseSession()
        try {
            assertThat(TH.countTableRows(session, "Tab_1")).isEqualTo(1)

            session.beginTransaction()

            session.command("insert into Tab_1 values ('B')").run()

            assertThat(TH.countTableRows(session, "Tab_1")).isEqualTo(2)

            session.rollback()

            assertThat(TH.countTableRows(session, "Tab_1")).isEqualTo(1)
        }
        finally {
            session.close()
        }
    }


    @Test
    fun access_metadata() {
        DB.inSessionDo { session ->

            val md = session.getSpecificService(DatabaseMetaData::class.java,
                                                ImplementationAccessibleService.Names.JDBC_METADATA)

            assertThat(md).isNotNull()

            var driverName: String?
            try {
                driverName = md!!.driverName
            }
            catch (e: SQLException) {
                throw RuntimeException(e.message, e)
            }

            assertThat(driverName).isNotNull()

        }
    }


}

package org.jetbrains.dekaf.core

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.dekaf.CommonIntegrationCase
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.sql.DatabaseMetaData
import java.sql.Driver
import java.sql.SQLException


/**
 * @author Leonid Bushuev
 */
@Tag("UnitTest")
class CommonPrimaryTest : CommonIntegrationCase() {

    @Test
    fun connect() {
        @Suppress("senseless_comparison")
        assert(DB != null)

        DB.connect()
        assertThat(DB.isConnected).isTrue()

        val info = DB.connectionInfo
        println("Connection info:")
        println("\tRDBMS: " + info.rdbmsName)
        println("\tDatabase: " + info.databaseName!!)
        println("\tSchema: " + info.schemaName!!)
        println("\tUser: " + info.userName!!)
        println("\tServer version: " + info.serverVersion)
        println("\tDriver version: " + info.driverVersion)
    }

    @Test
    fun disconnect() {
        DB.disconnect()
        assertThat(DB.isConnected).isFalse()
    }

    @Test
    fun access_metaData() {
        DB.connect()

        val b = StringBuilder(240)
        b.append("JDBC DatabaseMetaData:\n")

        DB.inSessionDo { session ->

            val md = session.getSpecificService(DatabaseMetaData::class.java,
                                                ImplementationAccessibleService.Names.JDBC_METADATA)
            assertThat(md).isNotNull()

            try {
                b.append("\tDatabaseProductName: ").append(md!!.databaseProductName).append('\n')
                b.append("\tDriverName: ").append(md.driverName).append('\n')
                b.append("\tUserName: ").append(md.userName).append('\n')
                b.append("\tDatabaseProductVersion: ").append(md.databaseProductVersion).append('\n')
                b.append("\tDriverVersion: ").append(md.driverVersion).append('\n')
                b.append("\tExtraNameCharacters: ").append(md.extraNameCharacters).append('\n')
                b.append("\tIdentifierQuoteString: ").append(md.identifierQuoteString).append('\n')
            }
            catch (e: SQLException) {
                throw RuntimeException(e)
            }


        }

        println(b.toString())
    }

    @Test
    fun getConnectionInfo_versions() {
        DB.connect()
        val info = DB.connectionInfo
        assertThat(info.serverVersion.isOrGreater(1))
        assertThat(info.driverVersion.isOrGreater(1))
    }

    @Test
    fun getConnectionInfo_rdbms() {
        DB.connect()
        val info = DB.connectionInfo
        assertThat(info.rdbmsName).isNotEmpty()
    }

    @Test
    fun getConnectionInfo_database() {
        DB.connect()
        val info = DB.connectionInfo
        assertThat(info.databaseName).isNotEmpty()
    }

    @Test
    fun getConnectionInfo_schema() {
        DB.connect()
        val info = DB.connectionInfo
        assertThat(info.schemaName).isNotEmpty()
    }


    @Test
    fun ping() {
        DB.connect()

        val session = DB.leaseSession()
        session.ping()
        session.close()

        DB.disconnect()
    }


    @Test
    fun select_1_in_session() {
        DB.connect()
        DB.inSessionDo { session ->

            val v = session.query("select 1 " + TH.fromSingleRowTable(),
                                  layoutSingleValueOf<Int>())
                    .run()!!.toInt()
            assertThat(v).isNotNull()
                    .isEqualTo(1)

        }
    }

    @Test
    fun select_1_in_transaction() {
        DB.connect()
        DB.inTransactionDo { tran ->

            val v = tran.query("select 1 " + TH.fromSingleRowTable(),
                               layoutSingleValueOf<Int>())
                    .run()!!.toInt()
            assertThat(v).isNotNull()
                    .isEqualTo(1)

        }
    }


    @Test
    fun zapSchema_basic() {
        DB.connect()
        TH.ensureNoTableOrView("T1", "V1")

        // create a table and a view
        TH.performScript(
                "create table T1 (F1 char(1))",
                "create view V1 as select * from T1"
        )

        // zap schema
        TH.zapSchema()

        // TODO verify that no tables and views
    }

    @Test
    fun zapSchema_foreignKeys() {
        DB.connect()
        TH.ensureNoTableOrView("T1", "T2", "T3")

        // create a table and a view
        TH.performScript(
                "create table T1 (X char(1) not null primary key)",
                "create table T2 (Y char(1) not null)",
                "create table T3 (Z char(1) not null primary key)",
                "alter table T2 add foreign key (Y) references T1",
                "alter table T2 add foreign key (Y) references T3"
        )

        // zap schema
        TH.zapSchema()

        // TODO verify that no tables
    }

    @Test
    fun get_driver() {
        val driver = DB.getSpecificService(Driver::class.java, ImplementationAccessibleService.Names.JDBC_DRIVER)
        assertThat(driver).isNotNull()
    }

}

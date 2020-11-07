package org.jetbrains.dekaf.jdbcTest.impl

import lb.yaka.expectations.equalsTo
import lb.yaka.expectations.iz
import lb.yaka.expectations.zero
import lb.yaka.gears.expect
import org.jetbrains.dekaf.inter.common.StatementCategory.stmtUpdate
import org.jetbrains.dekaf.test.utils.*
import org.junit.jupiter.api.*


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class JdbcSeanceTest : H2ConnectedTest() {

    @BeforeAll
    fun createBasicTable() {
        perform("create table if not exists Basic_Table (B tinyint, C char, V varchar(15))")
    }

    @AfterAll
    fun dropBasicTable() {
        perform("drop table if exists Basic_Table")
    }



    @Test @Order(2)
    fun basic_insert_1() {
        val stmtText = "insert into Basic_Table (B,C,V) values (?,?,?)"
        session.openSeance().use { seance ->
            seance.prepare(stmtText, stmtUpdate, null)
            seance.execute(listOf(`1`, 'C', "The First Row"))
        }
    }

    @Test @Order(3)
    fun basic_insert_3() {
        val stmtText = "insert into Basic_Table (B,C,V) values (?,?,?)"
        session.openSeance().use { seance ->
            seance.prepare(stmtText, stmtUpdate, null)
            seance.execute(listOf(`7`, 'X', "Row X"))
            seance.execute(listOf(`8`, 'Y', "Row Y"))
            seance.execute(listOf(`9`, 'Z', "Row Z"))
        }
    }


    @Test @Order(10)
    fun affectedRows_0() {
        val text = "update Basic_Table set B = -99 where 1 is null"
        var affectedRows = -1
        session.openSeance().use { seance ->
            seance.prepare(text, stmtUpdate, null)
            seance.execute(null)
            affectedRows = seance.affectedRows
        }
        expect that affectedRows iz zero
    }

    @Test @Order(11)
    fun affectedRows_1() {
        val text = "insert into Basic_Table (B,C,V) values (1,'A','Aa')"
        var affectedRows = -1
        session.openSeance().use { seance ->
            seance.prepare(text, stmtUpdate, null)
            seance.execute(null)
            affectedRows = seance.affectedRows
        }
        expect that affectedRows equalsTo 1
    }

    @Test @Order(12)
    fun affectedRows_3() {
        val text = "insert into Basic_Table (B,C,V) values (1,'A','Aa'), (2,'B','Bb'), (3,'C','Cc')"
        var affectedRows = -1
        session.openSeance().use { seance ->
            seance.prepare(text, stmtUpdate, null)
            seance.execute(null)
            affectedRows = seance.affectedRows
        }
        expect that affectedRows equalsTo 3
    }

}
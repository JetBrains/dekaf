package org.jetbrains.dekaf.jdbcTest.impl

import lb.yaka.expectations.equalsTo
import lb.yaka.gears.expect
import org.jetbrains.dekaf.inter.common.StatementCategory
import org.jetbrains.dekaf.test.utils.*
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class JdbcSeanceTest : H2ConnectedTest() {

    @Test @Order(1)
    fun basic_create() {
        val stmtText = "create table if not exists Basic_Table (B tinyint, C char, V varchar(15))"
        session.openSeance().use { seance ->
            seance.prepare(stmtText, StatementCategory.stmtSimple, null)
            seance.execute(null)
        }
    }

    @Test @Order(2)
    fun basic_insert_1() {
        val stmtText = "insert into Basic_Table (B,C,V) values (?,?,?)"
        session.openSeance().use { seance ->
            seance.prepare(stmtText, StatementCategory.stmtUpdate, null)
            seance.execute(listOf(`1`, 'C', "The First Row"))
            expect that seance.updatedRows equalsTo 1
        }
    }

    @Test @Order(3)
    fun basic_insert_3() {
        val stmtText = "insert into Basic_Table (B,C,V) values (?,?,?)"
        session.openSeance().use { seance ->
            seance.prepare(stmtText, StatementCategory.stmtUpdate, null)
            seance.execute(listOf(`7`, 'X', "Row X"))
            seance.execute(listOf(`8`, 'Y', "Row Y"))
            seance.execute(listOf(`9`, 'Z', "Row Z"))
        }
    }

    @Test @Order(9)
    fun basic_drop() {
        val stmtText = "drop table Basic_Table"
        session.openSeance().use { seance ->
            seance.prepare(stmtText, StatementCategory.stmtSimple, null)
            seance.execute(null)
        }
    }




}
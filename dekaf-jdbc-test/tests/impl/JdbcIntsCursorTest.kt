@file:Suppress("platform_class_mapped_to_kotlin", "RemoveRedundantQualifierName")

package org.jetbrains.dekaf.jdbcTest.impl

import lb.yaka.expectations.*
import lb.yaka.gears.*
import org.jetbrains.dekaf.inter.common.StatementCategory.stmtQuery
import org.jetbrains.dekaf.jdbc.impl.JdbcColumnCursor
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class JdbcIntsCursorTest : H2ConnectedTest() {

    @Test @Order(10)
    fun fetchSingleValue() {
        val text = "select * from values (1000001, 'labuda')"
        val r: IntArray? = session.openSeance().use { seance ->
            seance.prepare(text, stmtQuery, null)
            seance.execute(null)
            val cursor = seance.makeIntsCursor(0)
            cursor.fetchRow()
        }

        expect that r iz notNull; r!!
        expect that r hasSize 1
        expect that r[0] equalsTo 1000001
    }

    @Test @Order(11)
    fun fetchSingleNull() {
        val text = "select cast(null as bigint) as V"
        val r: IntArray? = session.openSeance().use { seance ->
            seance.prepare(text, stmtQuery, null)
            seance.execute(null)
            val cursor = seance.makeIntsCursor(0)
            cursor.setDefaultValue(-666)
            cursor.fetchRow()
        }

        expect that r iz notNull; r!!
        expect that r hasSize 1
        expect that r[0] equalsTo -666
    }

    @Test @Order(12)
    fun fetchSingleEmpty() {
        val text = "select * from values (1) where 1 is null"
        val r: IntArray? = session.openSeance().use { seance ->
            seance.prepare(text, stmtQuery, null)
            seance.execute(null)
            val cursor = seance.makeIntsCursor(0)
            cursor.setDefaultValue(-666)
            cursor.fetchRow()
        }

        expect that r iz Null
    }


    @Test @Order(20)
    fun fetchPortion() {
        val text = "select * from values (1000001, 'a'), (2000002, 'b'), (3000003, 'c')"
        val column = session.openSeance().use { seance ->
            seance.prepare(text, stmtQuery, null)
            seance.execute(null)
            val cursor = seance.makeIntsCursor(0)
            cursor.fetchPortion()
        }

        expect that column iz notNull; column!!
        expect that column hasSize 3

        expect that column[0] equalsTo 1000001
        expect that column[1] equalsTo 2000002
        expect that column[2] equalsTo 3000003
    }


    @Test @Order(30)
    fun portions() {
        val text = "select value from table(value int = (1,2,3,4,5,6,7,8,9,10,11,12))"
        session.openSeance().use { seance ->
            seance.setPortionSize(5)
            seance.prepare(text, stmtQuery, null)
            seance.execute(null)
            val cursor = seance.makeIntsCursor(0)

            val m1 = cursor.fetchPortion()

            expect that m1 iz notNull; m1!!
            expect that m1 hasSize 5

            expect that m1[0] equalsTo 1
            expect that m1[4] equalsTo 5

            val m2 = cursor.fetchPortion()

            expect that m2 iz notNull; m2!!
            expect that m2 hasSize 5

            expect that m2[0] equalsTo 6
            expect that m2[4] equalsTo 10

            val m3 = cursor.fetchPortion()

            expect that m3 iz notNull; m3!!
            expect that m3 hasSize 2

            expect that m3[0] equalsTo 11
            expect that m3[1] equalsTo 12

            val m4 = cursor.fetchPortion()

            expect that m4 iz Null
        }
    }


    @Test @Order(90)
    fun closeAtTheEnd() {
        val text = "select value from table(value int = (1,2,3,4,5,6,7))"
        session.openSeance().use { seance ->
            seance.setPortionSize(5)
            seance.prepare(text, stmtQuery, null)
            seance.execute(null)
            val cursor: JdbcColumnCursor<java.lang.Integer> = seance.makeColumnCursor(0, java.lang.Integer::class.java)
            cursor.prepare()

            expect that cursor.isClosed equalsTo false

            cursor.fetchPortion()

            expect that cursor.isClosed equalsTo false

            cursor.fetchPortion()

            expect that cursor.isClosed equalsTo true
        }
    }

}
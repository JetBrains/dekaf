@file:Suppress("platform_class_mapped_to_kotlin", "RemoveRedundantQualifierName")

package org.jetbrains.dekaf.jdbcTest.impl

import lb.yaka.expectations.*
import lb.yaka.gears.expect
import org.jetbrains.dekaf.inter.common.StatementCategory.stmtQuery
import org.jetbrains.dekaf.jdbc.impl.JdbcMatrixCursor
import org.jetbrains.dekaf.jdbcTest.JavaByte
import org.jetbrains.dekaf.jdbcTest.JavaInt
import org.jetbrains.dekaf.jdbcTest.JavaLong
import org.jetbrains.dekaf.jdbcTest.JavaShort
import org.jetbrains.dekaf.test.utils.*
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class JdbcMatrixCursorTest : H2ConnectedTest() {

    @Test @Order(10)
    fun fetchRow() {
        val text = "select * from values (1, 1001, 1000001), (2, 2002, 2000002), (3, 3003, 3000003)"
        val r: Array<Number?>? = session.openSeance().use { seance ->
            seance.prepare(text, stmtQuery, null)
            seance.execute(null)
            val cursor: JdbcMatrixCursor<Number> = seance.makeMatrixCursor(0, Number::class.java)
            cursor.prepare(arrayOf<Class<out Number>>(JavaByte::class.java, JavaShort::class.java, JavaLong::class.java))
            cursor.fetchRow()
        }

        expect that r iz notNull; r!!
        expect that r hasSize 3

        expect that r[0] iz JavaByte::class
        expect that r[1] iz JavaShort::class
        expect that r[2] iz JavaLong::class

        expect that r[0] equalsTo `1`
        expect that r[1] equalsTo 1001.toShort()
        expect that r[2] equalsTo 1000001L
    }

    @Test @Order(20)
    fun fetchPortion() {
        val text = "select * from values (1, 1001, 1000001), (2, 2002, 2000002), (3, 3003, 3000003)"
        val m = session.openSeance().use { seance ->
            seance.prepare(text, stmtQuery, null)
            seance.execute(null)
            val cursor: JdbcMatrixCursor<Number> = seance.makeMatrixCursor(0, Number::class.java)
            cursor.prepare(arrayOf<Class<out Number>>(JavaByte::class.java, JavaShort::class.java, JavaLong::class.java))
            cursor.fetchPortion()
        }

        expect that m iz notNull; m!!
        expect that m hasSize 3
        expect that m[0] hasSize 3

        expect that m[0][0] iz JavaByte::class
        expect that m[0][1] iz JavaShort::class
        expect that m[0][2] iz JavaLong::class
        expect that m[1][0] iz JavaByte::class
        expect that m[1][1] iz JavaShort::class
        expect that m[1][2] iz JavaLong::class
        expect that m[2][0] iz JavaByte::class
        expect that m[2][1] iz JavaShort::class
        expect that m[2][2] iz JavaLong::class

        expect that m[0][0] equalsTo `1`
        expect that m[1][0] equalsTo `2`
        expect that m[2][0] equalsTo `3`

        expect that m[0][1] equalsTo 1001.toShort()
        expect that m[1][1] equalsTo 2002.toShort()
        expect that m[2][1] equalsTo 3003.toShort()

        expect that m[0][2] equalsTo 1000001L
        expect that m[1][2] equalsTo 2000002L
        expect that m[2][2] equalsTo 3000003L
    }


    @Test @Order(30)
    fun portions() {
        val text = "select value from table(value int = (1,2,3,4,5,6,7,8,9,10,11,12))"
        session.openSeance().use { seance ->
            seance.setPortionSize(5)
            seance.prepare(text, stmtQuery, null)
            seance.execute(null)
            val cursor: JdbcMatrixCursor<JavaInt> = seance.makeMatrixCursor(0, JavaInt::class.java)
            cursor.prepare(arrayOf(JavaInt::class.java))

            val m1 = cursor.fetchPortion()

            expect that m1 iz notNull; m1!!
            expect that m1 hasSize 5

            expect that m1[0][0] equalsTo 1
            expect that m1[4][0] equalsTo 5

            val m2 = cursor.fetchPortion()

            expect that m2 iz notNull; m2!!
            expect that m2 hasSize 5

            expect that m2[0][0] equalsTo 6
            expect that m2[4][0] equalsTo 10

            val m3 = cursor.fetchPortion()

            expect that m3 iz notNull; m3!!
            expect that m3 hasSize 2

            expect that m3[0][0] equalsTo 11
            expect that m3[1][0] equalsTo 12

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
            val cursor: JdbcMatrixCursor<JavaInt> = seance.makeMatrixCursor(0, JavaInt::class.java)
            cursor.prepare(arrayOf(JavaInt::class.java))

            expect that cursor.isClosed equalsTo false

            cursor.fetchPortion()

            expect that cursor.isClosed equalsTo false

            cursor.fetchPortion()

            expect that cursor.isClosed equalsTo true
        }
    }

}
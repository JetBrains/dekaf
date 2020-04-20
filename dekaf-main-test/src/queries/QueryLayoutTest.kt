package org.jetbrains.dekaf.mainTest.queries

import lb.yaka.expectations.equalsTo
import lb.yaka.expectations.hasSize
import lb.yaka.expectations.iz
import lb.yaka.gears.empty
import lb.yaka.gears.expect
import org.jetbrains.dekaf.main.db.inSession
import org.jetbrains.dekaf.main.queries.Query
import org.jetbrains.dekaf.main.queries.layRowArrayOf
import org.jetbrains.dekaf.main.queries.layTableArrayOf
import org.jetbrains.dekaf.main.queries.layTableListOf
import org.jetbrains.dekaf.mainTest.base.UnitTestWithH2
import org.jetbrains.dekaf.mainTest.util.*
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test


class QueryLayoutTest : UnitTestWithH2 {

    private val query4x4 = """|select *
                              |from values (1, 2, 3, 4),
                              |            (5, 6, 7, 8),
                              |            (9, 10, 11, 12),
                              |            (13, 14, 15, 16)
                           """.trimMargin()

    @Test @Order(1)
    fun queryListOfArrayOfMandatoryNumber() {
        val query: Query<List<Array<out Number>>> =
                Query(query4x4, layTableListOf(layRowArrayOf<Number>(4, `0`)))
        val list: List<Array<out Number>> =
                dbf.inSession { session ->
                    session.query(query).run()
                }
        expect that list hasSize 4

        expect that list[0] hasSize 4
        expect that list[1] hasSize 4
        expect that list[2] hasSize 4
        expect that list[3] hasSize 4

        expect that list[0][0] equalsTo 1
        expect that list[0][1] equalsTo 2
        expect that list[0][2] equalsTo 3
        expect that list[0][3] equalsTo 4
        expect that list[1][0] equalsTo 5
        expect that list[1][1] equalsTo 6
        expect that list[1][2] equalsTo 7
        expect that list[1][3] equalsTo 8
    }

    @Test @Order(2)
    fun queryArrayOfArrayOfMandatoryNumber() {
        val query: Query<Array<Array<out Number>>> =
                Query(query4x4, layTableArrayOf(layRowArrayOf<Number>(4, `0`)))
        val list: Array<Array<out Number>> =
                dbf.inSession { session ->
                    session.query(query).run()
                }
        expect that list hasSize 4

        expect that list[0] hasSize 4
        expect that list[1] hasSize 4
        expect that list[2] hasSize 4
        expect that list[3] hasSize 4

        expect that list[0][0] equalsTo 1
        expect that list[0][1] equalsTo 2
        expect that list[0][2] equalsTo 3
        expect that list[0][3] equalsTo 4
        expect that list[1][0] equalsTo 5
        expect that list[1][1] equalsTo 6
        expect that list[1][2] equalsTo 7
        expect that list[1][3] equalsTo 8
    }

    @Test @Order(3)
    fun queryArrayOfArrayOfMandatoryNumber_empty() {
        val queryText = "$query4x4\nwhere 1 is null"
        val query: Query<Array<Array<out Number>>> =
                Query(queryText, layTableArrayOf(layRowArrayOf<Number>(4, `0`)))
        val list: Array<Array<out Number>> =
                dbf.inSession { session ->
                    session.query(query).run()
                }
        expect that list iz empty
    }

}

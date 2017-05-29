package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.assertions.IsFalse
import org.jetbrains.dekaf.assertions.IsNotNull
import org.jetbrains.dekaf.assertions.IsTrue
import org.jetbrains.dekaf.assertions.expected
import org.jetbrains.dekaf.core.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test


class LayoutsTest {

    companion object {

        @JvmStatic
        internal val provider = BaseProvider()

        @JvmStatic
        internal val db = provider.provide("jdbc:h2:mem:Testing")

        @BeforeAll @JvmStatic
        fun connectToH2() {
            db.connect()
        }

    }

    @Test
    fun existence_0() {
        val layout = layoutExistence()
        val result: Boolean? = db.inTransaction { tran ->
            tran.query("select 0 where 1 is null", layout).run()
        }

        result expected IsNotNull
        result expected IsFalse
    }

    @Test
    fun existence_1() {
        val layout = layoutExistence()
        val result: Boolean? = db.inTransaction { tran ->
            tran.query("select 1", layout).run()
        }

        result expected IsNotNull
        result expected IsTrue
    }


    @Test
    fun singleValue() {
        val layout = layoutSingleValueOf<Number>()
        val queryText = "select 1234 union all select 5678 order by 1"
        val result: Number? = db.inTransaction { tran ->
            tran.query(queryText, layout).run()
        }

        result expected 1234
    }


    @Test
    fun arrayOfNumber() {
        val layout = layoutArrayOf(rowValueOf<Number>())
        val queryText = "select * from table (nr int=(1234,5678,9012)) order by 1"
        val result: Array<out Number>? = db.inTransaction { tran ->
            tran.query(queryText, layout).run()
        }

        result expected IsNotNull
        result!!
        result expected arrayOf<Number>(1234,5678,9012)
    }

    @Test
    fun listOfNumber() {
        val layout = layoutListOf(rowValueOf<Number>())
        val queryText = "select * from table (nr int=(1234,5678,9012)) order by 1"
        val result: List<Number>? = db.inTransaction { tran ->
            tran.query(queryText, layout).run()
        }

        result expected IsNotNull
        result!!
        result expected listOf<Number>(1234,5678,9012)
    }

}
@file:Suppress("platform_class_mapped_to_kotlin")

package org.jetbrains.dekaf.core

import org.assertj.core.api.Assertions
import org.jetbrains.dekaf.CommonIntegrationCase
import org.jetbrains.dekaf.Oracle
import org.jetbrains.dekaf.assertions.IsNotNull
import org.jetbrains.dekaf.assertions.expected
import org.jetbrains.dekaf.sql.SqlQuery
import org.jetbrains.dekaf.text.Rewriters
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.lang.Number
import java.sql.ResultSetMetaData
import java.sql.SQLException
import kotlin.Any
import kotlin.Long
import kotlin.RuntimeException
import kotlin.String
import kotlin.Suppress


/**
 * @author Leonid Bushuev
 */
class CommonQueryRunnerTest : CommonIntegrationCase() {


    companion object {

        @BeforeAll @JvmStatic
        fun setUp() {
            DB.connect()
            TH.prepareX1()
            TH.prepareX1000()
            TH.prepareX1000000()
        }

        protected var isOracle = DB.rdbms() == Oracle.RDBMS

    }


    @Test
    fun query_existence_0() {
        val queryText = "select 1 from " + (if (isOracle) "dual" else "X1") + " where 1 is null"
        val q = SqlQuery(queryText, layoutExistence())
        val b = query(q)
        Assertions.assertThat(b).isNotNull
                .isFalse
    }

    @Test
    fun query_existence_1() {
        val queryText = "select 1 " + if (isOracle) "from dual" else ""
        val q = SqlQuery(queryText, layoutExistence())
        val b = query(q)
        Assertions.assertThat(b).isNotNull
                .isTrue
    }

    @Test
    fun query_primitive_numbers_basic() {
        val queryText = "select 1 as B, 2 as S, 3 as I, 4 as L"
        val layout = layoutOneRowOf(rowStructOf(PrimitiveNumbers::class.java))
        val query = SqlQuery(queryText, layout)
        val pn = query(query)

        pn!!

        Assertions.assertThat(pn.B).isEqualTo(1.toByte())
        Assertions.assertThat(pn.S).isEqualTo(2.toShort())
        Assertions.assertThat(pn.I).isEqualTo(3)
        Assertions.assertThat(pn.L).isEqualTo(4L)
    }

    @Test
    fun query_primitive_numbers_positive() {
        val queryText = "select 127 as B, 32767 as S, 2147483647 as I, 9223372036854775807 as L"
        val query = SqlQuery(queryText, layoutOneRowOf(rowStructOf(PrimitiveNumbers::class.java)))
        val pn = query(query)

        pn!!

        Assertions.assertThat(pn.B).isEqualTo(127.toByte())
        Assertions.assertThat(pn.S).isEqualTo(32767.toShort())
        Assertions.assertThat(pn.I).isEqualTo(2147483647)
        Assertions.assertThat(pn.L).isEqualTo(9223372036854775807L)
    }

    @Test
    fun query_primitive_numbers_negative() {
        val queryText = "select -128 as B, -32768 as S, -2147483648 as I, -9223372036854775808 as L"
        val query = SqlQuery(queryText, layoutOneRowOf(rowStructOf(PrimitiveNumbers::class.java)))
        val pn = query(query)

        Assertions.assertThat(pn!!.B).isEqualTo((-128).toByte())
        Assertions.assertThat(pn.S).isEqualTo((-32768).toShort())
        Assertions.assertThat(pn.I).isEqualTo(-2147483648)
        Assertions.assertThat(pn.L).isEqualTo(Long.MIN_VALUE)   // hello,  KT-17172
    }

    @Test
    fun query_boxed_numbers_positive() {
        val queryText = "select 127 as B, 32767 as S, 2147483647 as I, 9223372036854775807 as L from X1"
        val query = SqlQuery(queryText, layoutOneRowOf(rowStructOf(BoxedNumbers::class.java)))
        val bn = query(query)

        Assertions.assertThat(bn!!.B).isEqualTo(127.toByte())
        Assertions.assertThat(bn.S).isEqualTo(32767.toShort())
        Assertions.assertThat(bn.I).isEqualTo(2147483647)
        Assertions.assertThat(bn.L).isEqualTo(9223372036854775807L)
    }


    @Test
    fun query_raw_numbers() {
        val queryText = "select 127 as B, 32767 as S, 2147483647 as I, 9223372036854775807 as L from X1"
        val query = SqlQuery(queryText, layoutOneRowOf(rowArrayOf<Number>()))
        val numbers = query(query)

        numbers!!

        Assertions.assertThat(numbers).hasSize(4)
        Assertions.assertThat(numbers[0]).isInstanceOf(Number::class.java)
        Assertions.assertThat(numbers[1]).isInstanceOf(Number::class.java)
        Assertions.assertThat(numbers[2]).isInstanceOf(Number::class.java)
        Assertions.assertThat(numbers[3]).isInstanceOf(Number::class.java)
        Assertions.assertThat((numbers[0] as Number).intValue()).isEqualTo(127)
        Assertions.assertThat((numbers[1] as Number).intValue()).isEqualTo(32767)
        Assertions.assertThat((numbers[2] as Number).intValue()).isEqualTo(2147483647)
        Assertions.assertThat((numbers[3] as Number).longValue()).isEqualTo(9223372036854775807L)
    }

    @Test
    fun query_raw_strings() {
        val queryText = "select 'C', 'String' from X1"
        val query = SqlQuery(queryText, layoutOneRowOf(rowArrayOf<Any>()))
        val strings = query(query)

        strings!!

        Assertions.assertThat(strings).hasSize(2)

        Assertions.assertThat(strings[0]).isInstanceOf(String::class.java)
        Assertions.assertThat(strings[1]).isInstanceOf(String::class.java)

        Assertions.assertThat(strings[0]).isEqualTo("C")
        Assertions.assertThat(strings[1]).isEqualTo("String")
    }


    @Test
    fun query_calendar_values_now() {
        var queryText = "select NOW as javaDate, NOW as sqlDate, NOW as sqlTimestamp, NOW as sqlTime"
        if (isOracle) queryText += " from dual"
        val query = SqlQuery(queryText, layoutOneRowOf(rowStructOf(CalendarValues::class.java)))
                .rewrite(Rewriters.replace("NOW", sqlNow()))

        val cv = query(query)

        Assertions.assertThat(cv!!.javaDate).isExactlyInstanceOf(java.util.Date::class.java)
        Assertions.assertThat(cv.sqlDate).isExactlyInstanceOf(java.sql.Date::class.java)
        Assertions.assertThat(cv.sqlTimestamp).isExactlyInstanceOf(java.sql.Timestamp::class.java)
        Assertions.assertThat(cv.sqlTime).isExactlyInstanceOf(java.sql.Time::class.java)
    }

    @Test
    fun query_calendar_values_parameters() {
        var queryText = queryCalendarValuesFromParameters()
        if (isOracle) queryText += " from dual"
        val query = SqlQuery(queryText, layoutOneRowOf(rowStructOf(CalendarValues::class.java)))

        val cv = query(query,
                       java.sql.Timestamp(System.currentTimeMillis()),
                       java.sql.Timestamp(System.currentTimeMillis()),
                       java.sql.Timestamp(System.currentTimeMillis()),
                       java.sql.Timestamp(System.currentTimeMillis())
        )

        Assertions.assertThat(cv.javaDate).isExactlyInstanceOf(java.util.Date::class.java)
        Assertions.assertThat(cv.sqlDate).isExactlyInstanceOf(java.sql.Date::class.java)
        Assertions.assertThat(cv.sqlTimestamp).isExactlyInstanceOf(java.sql.Timestamp::class.java)
        Assertions.assertThat(cv.sqlTime).isExactlyInstanceOf(java.sql.Time::class.java)
    }


    protected fun queryCalendarValuesFromParameters(): String {
        return "select ? as javaDate, ? as sqlDate, ? as sqlTimestamp, ? as sqlTime"
    }

    protected fun sqlNow(): String {

        return "current_timestamp"
    }


    @Test
    fun query_1000_values() {
        val values = CommonIntegrationCase.DB.inTransaction<List<Integer>> { tran -> tran.query("select X from X1000 order by 1", layoutListOf(rowValueOf(Integer::class.java))).run() }
        Assertions.assertThat(values).isNotNull
                .hasSize(1000)
                .contains(Integer(1), Integer(2), Integer(3), Integer(998), Integer(999), Integer(1000))
    }


    @Test
    fun query_1000000_values() {
        val values = CommonIntegrationCase.DB.inTransaction<List<Integer>> { tran -> tran.query("select X from X1000000 order by 1", layoutListOf(rowValueOf(Integer::class.java))).run() }
        Assertions.assertThat(values).isNotNull
                .hasSize(1000000)
                .contains(Integer(1), Integer(2), Integer(3), Integer(999998), Integer(999999), Integer(1000000))
    }


    @Test
    fun access_metadata() {
        val query = SqlQuery("select 123456789 as X", layoutListOf(rowValueOf(Number::class.java)))

        CommonIntegrationCase.DB.inSessionDo { session ->

            val qr = session.query(query).packBy(10).execute()
            val md = qr.getSpecificService(ResultSetMetaData::class.java,
                                           ImplementationAccessibleService.Names.JDBC_METADATA)

            md expected IsNotNull
            md!!

            val columnName: String?
            try {
                columnName = md.getColumnName(1)
            }
            catch (e: SQLException) {
                throw RuntimeException(e.message, e)
            }

            Assertions.assertThat(columnName).isEqualToIgnoringCase("X")
        }
    }


    protected fun <T> query(query: SqlQuery<T>): T? {
        return CommonIntegrationCase.DB.inTransaction<T> { tran -> tran.query(query).run() }
    }

    protected fun <T> query(query: SqlQuery<T>, vararg params: Any): T {
        return CommonIntegrationCase.DB.inTransaction<T> { tran -> tran.query(query).withParams(*params).run() }
    }

}

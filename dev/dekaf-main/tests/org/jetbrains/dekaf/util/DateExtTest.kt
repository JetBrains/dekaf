package org.jetbrains.dekaf.util


import org.jetbrains.dekaf.assertions.expected
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.*

/**
 * @author Leonid Bushuev
 **/
@Tag("UnitTest")
class DateExtTest {

    @Test
    fun epochDateOf_basic() {
        val timestamp: Date = epochDateOf(1999, 12, 31, 23, 59, 59)
        val calendar = GregorianCalendar()
        calendar.time = timestamp

        calendar.get(Calendar.YEAR)         expected 1999
        calendar.get(Calendar.MONTH)        expected Calendar.DECEMBER
        calendar.get(Calendar.DAY_OF_MONTH) expected 31
        calendar.get(Calendar.HOUR_OF_DAY)  expected 23
        calendar.get(Calendar.MINUTE)       expected 59
        calendar.get(Calendar.SECOND)       expected 59
    }

    @Test
    fun epochDateOf_milliseconds() {
        val timestamp: Date = epochDateOf(1999, 12, 31, 23, 59, 59, 999)
        val calendar = GregorianCalendar()
        calendar.time = timestamp

        calendar.get(Calendar.YEAR)         expected 1999
        calendar.get(Calendar.MONTH)        expected Calendar.DECEMBER
        calendar.get(Calendar.DAY_OF_MONTH) expected 31
        calendar.get(Calendar.HOUR_OF_DAY)  expected 23
        calendar.get(Calendar.MINUTE)       expected 59
        calendar.get(Calendar.SECOND)       expected 59
    }

    @Test
    fun sqlDateOf_basic() {
        val date: java.sql.Date = sqlDateOf(1974,6,3)
        val calendar = GregorianCalendar()
        calendar.time = date

        calendar.get(Calendar.YEAR)         expected 1974
        calendar.get(Calendar.MONTH)        expected Calendar.JUNE
        calendar.get(Calendar.DAY_OF_MONTH) expected 3
    }

    @Test
    fun sqlTimestampOf_basic() {
        val timestamp: java.sql.Timestamp = sqlTimestampOf(2000, 12, 31, 23, 59, 59)
        val calendar = GregorianCalendar()
        calendar.time = timestamp

        calendar.get(Calendar.YEAR)         expected 2000
        calendar.get(Calendar.MONTH)        expected Calendar.DECEMBER
        calendar.get(Calendar.DAY_OF_MONTH) expected 31
        calendar.get(Calendar.HOUR_OF_DAY)  expected 23
        calendar.get(Calendar.MINUTE)       expected 59
        calendar.get(Calendar.SECOND)       expected 59
    }

    @Test
    fun sqlTimestampOf_milliseconds() {
        val timestamp: java.sql.Timestamp = sqlTimestampOf(2000, 12, 31, 23, 59, 59, 999)
        val calendar = GregorianCalendar()
        calendar.time = timestamp

        calendar.get(Calendar.YEAR)         expected 2000
        calendar.get(Calendar.MONTH)        expected Calendar.DECEMBER
        calendar.get(Calendar.DAY_OF_MONTH) expected 31
        calendar.get(Calendar.HOUR_OF_DAY)  expected 23
        calendar.get(Calendar.MINUTE)       expected 59
        calendar.get(Calendar.SECOND)       expected 59
        calendar.get(Calendar.MILLISECOND)  expected 999
    }

    @Test
    fun export_SqlDate() {
        val date = sqlDateOf(1974, 1, 1)
        val string = date.export()
        string expected "1974-01-01"
    }

    @Test
    fun export_SqlDate_PostgresCrazyFuture() {
        val date = sqlDateOf(292278994, 8, 17)
        val string = date.export()
        string expected "292278994-08-17"
    }

    @Test
    fun export_SqlTimestamp_noTime() {
        val date = sqlTimestampOf(2010, 12, 31)
        val string = date.export()
        string expected "2010-12-31.00:00:00"
    }

    @Test
    fun export_SqlTimestamp_withTime() {
        val date = sqlTimestampOf(2010, 12, 31, 23, 59, 59)
        val string = date.export()
        string expected "2010-12-31.23:59:59"
    }

    @Test
    fun export_SqlTimestamp_withTimeMilliseconds() {
        val date = sqlTimestampOf(2010, 12, 31, 23, 59, 59, 666)
        val string = date.export()
        string expected "2010-12-31.23:59:59.666"
    }

    @Test
    fun import_Date() {
        val date = importSqlDateTime("1974-01-01")
        date expected sqlDateOf(1974,1,1)
    }

    @Test
    fun import_Timestamp_noTime_1() {
        val timestamp = importSqlDateTime("1974-01-01.00:00")
        timestamp expected sqlTimestampOf(1974,1,1)
    }

    @Test
    fun import_Timestamp_noTime_2() {
        val timestamp = importSqlDateTime("1974-01-01.00:00:00")
        timestamp expected sqlTimestampOf(1974,1,1)
    }

    @Test
    fun import_Timestamp_withTime_1() {
        val timestamp = importSqlDateTime("1974-01-01.06:01")
        timestamp expected sqlTimestampOf(1974,1,1,6,1,0)
    }

    @Test
    fun import_Timestamp_withTime_2() {
        val timestamp = importSqlDateTime("1974-01-01.06:01:01")
        timestamp expected sqlTimestampOf(1974,1,1,6,1,1)
    }

    @Test
    fun import_Timestamp_withTime_milliseconds() {
        val timestamp = importSqlDateTime("1974-01-01.06:01:01.555")
        timestamp expected sqlTimestampOf(1974,1,1,6,1,1,555)
    }

    @Test
    fun Date_hasTime() {
        sqlDateOf(1974,3,6).hasTime() expected false
        sqlTimestampOf(1974,3,6).hasTime() expected false
        sqlTimestampOf(1974,3,6,12,0,0).hasTime() expected true
    }

    @Test
    fun Date_hasMilliseconds() {
        sqlDateOf(2000,1,1).hasMilliseconds() expected false
        sqlTimestampOf(2000,1,1,23,59,59).hasMilliseconds() expected false
        sqlTimestampOf(2000,1,1,23,59,59,1).hasMilliseconds() expected true
    }


}
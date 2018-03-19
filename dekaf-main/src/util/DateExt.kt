@file:JvmName("DateExt")
package org.jetbrains.dekaf.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.GregorianCalendar.*



/**
 * Creates an instance of a usual Java 1.6 date (class [java.util.Date]).
 */
fun epochDateOf(year: Int, month: Int, day: Int, hours: Int = 0, minutes: Int = 0, seconds: Int = 0): Date {
    val calendar = GregorianCalendar(year, month-1, day, hours, minutes, seconds)
    return Date(calendar.timeInMillis)
}

/**
 * Creates an instance of a usual Java 1.6 date (class [java.util.Date]), with milliseconds.
 */
fun epochDateOf(year: Int, month: Int, day: Int, hours: Int, minutes: Int, seconds: Int, milliseconds: Int): Date {
    val calendar = GregorianCalendar(year, month-1, day, hours, minutes, seconds)
    return Date(calendar.timeInMillis + milliseconds)
}

/**
 * Creates an instance of [java.sql.Date].
 */
fun sqlDateOf(year: Int, month: Int, day: Int): java.sql.Date {
    val calendar = GregorianCalendar(year, month-1, day)
    return java.sql.Date(calendar.timeInMillis)
}

/**
 * Creates an instance of [java.sql.Timestamp].
 */
fun sqlTimestampOf(year: Int, month: Int, day: Int, hours: Int = 0, minutes: Int = 0, seconds: Int = 0): java.sql.Timestamp {
    val calendar = GregorianCalendar(year, month-1, day, hours, minutes, seconds)
    return java.sql.Timestamp(calendar.timeInMillis)
}

/**
 * Creates an instance of [java.sql.Timestamp], with milliseconds.
 */
fun sqlTimestampOf(year: Int, month: Int, day: Int, hours: Int, minutes: Int, seconds: Int, milliseconds: Int): java.sql.Timestamp {
    val calendar = GregorianCalendar(year, month-1, day, hours, minutes, seconds)
    return java.sql.Timestamp(calendar.timeInMillis + milliseconds)
}


/**
 * Exports the date or data and time to a string
 * that is not depend on current locale, time zone or other volatile environment settings.
 * Such string representation is best for saving to a file and is easy to parse.
 * @see [exportDateOnly]
 * @see [exportDateTime]
 */
fun java.util.Date.export() =
        when (this) {
            is java.sql.Date      -> this.exportDateOnly()
            is java.sql.Timestamp -> this.exportDateTime()
            else                  -> if (this.hasTime()) this.exportDateTime() else exportDateOnly()
        }

/**
 * Exports the date (without time) to a string
 * that is not depend on current locale, time zone or other volatile environment settings.
 * Such string representation is best for saving to a file and is easy to parse.
 * @see [export]
 * @see [exportDateTime]
 */
fun java.util.Date.exportDateOnly(): String =
        this toString formalTimestampFormat10

/**
 * Exports the date and time to a string
 * that is not depend on current locale, time zone or other volatile environment settings.
 * The time is exported even if it is zero.
 * Such string representation is best for saving to a file and is easy to parse.
 * @see [export]
 * @see [exportDateOnly]
 */
fun java.util.Date.exportDateTime(): String  {
    return this.exportDateTime(this.hasMilliseconds())
}

/**
 * Exports the date and time to a string
 * that is not depend on current locale, time zone or other volatile environment settings.
 * The time is exported even if it is zero.
 * Such string representation is best for saving to a file and is easy to parse.
 * @see [export]
 * @see [exportDateTime]
 */
fun java.util.Date.exportDateTime(withMilliseconds: Boolean): String  {
    val format = if (withMilliseconds) formalTimestampFormat23 else formalTimestampFormat19
    return this toString format
}

/**
 * Checks whether the timestamp contains information about time in the day.
 */
fun java.util.Date.hasTime(): Boolean {
    val c = GregorianCalendar()
    c.timeInMillis = this.time
    return c.get(HOUR_OF_DAY) > 0 || c.get(MINUTE) > 0 || c.get(SECOND) > 0 || c.get(MILLISECOND) > 0
}

/**
 * Checks whether the timestamp contains milliseconds.
 */
fun java.util.Date.hasMilliseconds(): Boolean {
    return this.time % 1000L > 0L
}


/**
 * Converts the date to a string using the specified format.
 * @throws IllegalAccessException when failed to convert.
 */
infix fun java.util.Date.toString (format: DateFormat): String {
    try {
        return format.format(this)
    }
    catch (e: Exception) {
        val message =
                """|Failed to format date of class ${this.javaClass.name} with value "$this", numeric value ${this.time}, using format $format.
                   |Exception ${e.javaClass.simpleName}: ${e.message}
                """.trimMargin()
        throw IllegalArgumentException(message, e)
    }
}


private val formalTimestampFormat10 = SimpleDateFormat("yyyy-MM-dd")
private val formalTimestampFormat19 = SimpleDateFormat("yyyy-MM-dd.HH:mm:ss")
private val formalTimestampFormat23 = SimpleDateFormat("yyyy-MM-dd.HH:mm:ss.SSS")


/**
 * Imports a date or date and time that was exported by {#export} and similar methods.
 * @throws IllegalDateTimeFormatException when the giben string is not a valid exported date or date and time.
 */
fun importSqlDateTime(string: CharSequence): Date {
    val matcher = formalSqlDateTimePattern.matchEntire(string)
            ?: throw IllegalDateTimeFormatException("""The string "$string" is not a valid date and time (wrong format).""")
    val values = matcher.groupValues
    try {
        val y = values[1].toInt()
        val m = values[2].toInt()
        val d = values[3].toInt()
        if (values[4].isEmpty()) {
            val c = GregorianCalendar(y,m-1,d,0,0,0)
            return java.sql.Date(c.timeInMillis)
        }
        else {
            val h = values[5].toInt()
            val n = values[6].toInt()
            val s = values[8].toIntOrNull() ?: 0
            val l = values[10].toIntOrNull() ?: 0
            val c = GregorianCalendar(y,m-1,d,h,n,s)
            val millis = c.timeInMillis + l
            return java.sql.Timestamp(millis)
        }
    }
    catch (e: Exception) {
        throw IllegalDateTimeFormatException("""The string "$string" is not a valid date and time (wrong values).""", e)
    }
}


private val formalSqlDateTimePattern = Regex("""^\s*(-?\d+)-(\d+)-(\d+)(\s*\.\s*(\d+):(\d+)(:(\d+)(\.(\d{3}))?)?)?\s*$""")


class IllegalDateTimeFormatException : IllegalArgumentException {
    constructor(s: String?) : super(s)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
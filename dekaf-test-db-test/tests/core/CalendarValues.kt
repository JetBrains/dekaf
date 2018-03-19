package org.jetbrains.dekaf.core

class CalendarValues {

    @JvmField var javaDate:     java.util.Date?     = null
    @JvmField var sqlDate:      java.sql.Date?      = null
    @JvmField var sqlTimestamp: java.sql.Timestamp? = null
    @JvmField var sqlTime:      java.sql.Time?      = null

}
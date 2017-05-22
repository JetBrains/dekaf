package org.jetbrains.dekaf.jdbc

import org.jetbrains.dekaf.assertions.IsNotNull
import org.jetbrains.dekaf.assertions.expected
import org.jetbrains.dekaf.assertions.expectedClass
import org.jetbrains.dekaf.core.TaskKind
import org.jetbrains.dekaf.inter.InterCursor
import org.jetbrains.dekaf.inter.InterCursorLayout
import org.jetbrains.dekaf.inter.InterResultKind.RES_ONE_ROW
import org.jetbrains.dekaf.inter.InterRowKind.ROW_ONE_VALUE
import org.jetbrains.dekaf.inter.InterTask
import org.jetbrains.dekaf.util.NameAndClass
import org.junit.jupiter.api.Test


class JdbcSeanceTest {

    @Test
    fun select_123_asSingleValue() {
        val task = InterTask(TaskKind.TASK_QUERY, "select 123")
        val layout = InterCursorLayout(RES_ONE_ROW, ROW_ONE_VALUE, arrayOf(NameAndClass("1", Int::class.java)))

        H2mem.hmInSessionDo { session ->
            val seance = session.openSeance()
            seance.prepare(task)
            seance.execute()
            val cursor1: InterCursor? = seance.openCursor(0, layout)
            val cursor = cursor1 expected IsNotNull
            val portion = cursor.retrievePortion() 
            val value = portion expectedClass Int::class
            value expected 123
            seance.close()
        }
    }

}
package org.jetbrains.dekaf.main.queries.impl

import org.jetbrains.dekaf.inter.intf.InterMatrixCursor
import org.jetbrains.dekaf.inter.intf.InterSeance
import org.jetbrains.dekaf.inter.utils.ArrayHacks


class RowArrayNHandler<E: Any>: RowHandler<Array<out E?>, E?> {

    private val baseClass: Class<E>
    private val columnCount: Int

    override val rowClass: Class<Array<out E?>>

    private lateinit var cursor: InterMatrixCursor<E>
    

    constructor(baseClass: Class<E>, columnCount: Int) {
        this.baseClass = baseClass
        this.columnCount = columnCount
        rowClass = ArrayHacks.createEmptyArray(baseClass).javaClass
    }


    override fun prepare(seance: InterSeance, position: Int): InterMatrixCursor<E?> {
        val cursor = seance.makeMatrixCursor(position, baseClass)
        cursor.prepare()
        this.cursor = cursor
        return cursor
    }

    override fun handleRow(values: Array<out E?>): Array<out E?> {
        return values
    }
    
}
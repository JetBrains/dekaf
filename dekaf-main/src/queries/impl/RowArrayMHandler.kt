package org.jetbrains.dekaf.main.queries.impl

import org.jetbrains.dekaf.inter.intf.InterMatrixCursor
import org.jetbrains.dekaf.inter.intf.InterSeance
import org.jetbrains.dekaf.inter.utils.ArrayHacks


class RowArrayMHandler<E: Any>: RowHandler<Array<out E>, E> {

    private val baseClass: Class<E>
    private val columnCount: Int
    private val defaultValue: E

    override val rowClass: Class<Array<out E>>

    private lateinit var cursor: InterMatrixCursor<E>

    private var cN: Int


    constructor(baseClass: Class<E>, columnCount: Int, defaultValue: E) {
        this.baseClass = baseClass
        this.columnCount = columnCount
        this.defaultValue = defaultValue
        cN = columnCount
        rowClass = ArrayHacks.createEmptyArray(baseClass).javaClass
    }

    override fun prepare(seance: InterSeance, position: Int): InterMatrixCursor<E> {
        val cursor = seance.makeMatrixCursor(position, baseClass)
        cursor.prepare()
        cursor.setDefaultValue(defaultValue)
        this.cursor = cursor
        return cursor
    }

    override fun handleRow(values: Array<out E>): Array<out E> {
        return values
    }



}
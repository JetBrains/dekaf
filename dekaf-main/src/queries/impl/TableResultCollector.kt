package org.jetbrains.dekaf.main.queries.impl

import org.jetbrains.dekaf.inter.intf.InterMatrixCursor
import org.jetbrains.dekaf.inter.intf.InterSeance


abstract class TableResultCollector<T, R, B>: ResultCollector<T> {

    protected val handler: RowHandler<R, B>

    protected lateinit var cursor: InterMatrixCursor<B>


    constructor(handler: RowHandler<R, B>) {
        this.handler = handler
    }


    override fun prepare(seance: InterSeance, positionIndex: Int) {
        cursor = handler.prepare(seance, positionIndex)
    }

    protected fun retrieveIntoArrayList(): ArrayList<R> {
        val result = ArrayList<R>()
        while (true) {
            val portion: Array<Array<B>> = cursor.fetchPortion() ?: break
            val n = portion.size
            if (n == 0) continue
            result.ensureCapacity(result.size + n)
            for (r in portion) {
                val row = handler.handleRow(r)
                result.add(row)
            }
        }
        //cursor.close()
        return result
    }

    override fun close() {
        if (::cursor.isInitialized) {
            cursor.close()
        }
    }

}

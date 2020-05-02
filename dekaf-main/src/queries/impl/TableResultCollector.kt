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


    protected inner class PortionedIterator : Iterator<R> {

        private var portion: Array<Array<B>>? = null
        private var index: Int = 0

        private val lock = Object()

        internal fun prepare() {
            synchronized(lock) {
                fetchNextPortion()
            }
        }

        private fun fetchNextPortion() {
            portion = cursor.fetchPortion()
            index = 0
            if (portion == null) cursor.close()
        }

        override fun hasNext(): Boolean {
            synchronized(lock) {
                val portion = this.portion
                return portion != null && index < portion.size
            }
        }

        private fun nextRow(): Array<B> {
            synchronized(lock) {
                val portion = this.portion
                              ?: throw NoSuchElementException("Not more elements in the cursor")
                val r = portion[index]
                index++
                if (index >= portion.size) fetchNextPortion()
                return r
            }
        }

        override fun next(): R {
            val row = nextRow()
            return handler.handleRow(row)
        }
    }

}

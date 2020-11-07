package org.jetbrains.dekaf.main.queries.impl

import org.jetbrains.dekaf.inter.utils.ArrayHacks


class TableArrayResultCollector<R, B>: TableResultCollector<Array<R>, R, B> {

    constructor(handler: RowHandler<R, B>) : super(handler)


    override fun retrieve(): Array<R> {
        val list = retrieveIntoArrayList()
        val emptyArray: Array<R> = ArrayHacks.createEmptyArray(handler.rowClass)
        val n = list.size
        if (n == 0) return emptyArray
        return list.toArray(emptyArray)
    }

}
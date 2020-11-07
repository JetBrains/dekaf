package org.jetbrains.dekaf.main.queries.impl


class TableListResultCollector<R, B>: TableResultCollector<List<R>, R, B> {

    constructor(handler: RowHandler<R, B>) : super(handler)


    override fun retrieve(): List<R> {
        return retrieveIntoArrayList()
    }

}
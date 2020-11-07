package org.jetbrains.dekaf.main.queries.impl


class TableIteratorResultCollector<R, B>: TableResultCollector<Iterator<R>, R, B> {


    constructor(handler: RowHandler<R, B>) : super(handler)


    override fun retrieve(): Iterator<R> {
        val iterator = PortionedIterator()
        iterator.prepare()
        return iterator
    }


}

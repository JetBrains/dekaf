package org.jetbrains.dekaf.main.queries.impl

import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport


class TableStreamResultCollector<R, B>: TableResultCollector<Stream<R>, R, B> {

    constructor(handler: RowHandler<R, B>) : super(handler)

    override fun retrieve(): Stream<R> {
        val iterator = PortionedIterator()
        iterator.prepare()
        if (!iterator.hasNext()) return Stream.empty()

        val spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        val stream = StreamSupport.stream(spliterator, false)
        stream.onClose { cursor.close() }
        return stream
    }
    
}
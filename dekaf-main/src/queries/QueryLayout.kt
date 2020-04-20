package org.jetbrains.dekaf.main.queries

import org.jetbrains.dekaf.main.queries.impl.*


sealed class QueryLayout<T> {
    abstract fun makeResultCollector(): ResultCollector<T>
}


class QueryTableLayout<T, R:Any, B> (val table: TableLayout<T,R,B>, val row: RowLayout<R,B>) : QueryLayout<T>() {
    override fun makeResultCollector(): ResultCollector<T> =
            table.makeResultCollector(row)
}





sealed class TableLayout<T, R:Any, B> {
    abstract fun makeResultCollector(row: RowLayout<R,B>): TableResultCollector<T, R, B>
}

class ListTableLayout<R:Any, B> : TableLayout<List<R>, R, B>() {
    override fun makeResultCollector(row: RowLayout<R,B>): TableResultCollector<List<R>, R, B> =
            TableListResultCollector(row.makeRowHandler())
}

class ArrayTableLayout<R:Any, B> : TableLayout<Array<R>, R, B>() {
    override fun makeResultCollector(row: RowLayout<R, B>): TableResultCollector<Array<R>, R, B> =
            TableArrayResultCollector(row.makeRowHandler())
}



sealed class RowLayout<R:Any, B> {
    abstract fun makeRowHandler(): RowHandler<R, B>
}


class ArrayOfMRowLayout<E:Any> (private val elementClass: Class<E>,
                                private val columnCount: Int,
                                private val defaultValue: E) : RowLayout<Array<out E>, E>() {

    override fun makeRowHandler(): RowHandler<Array<out E>, E> =
            RowArrayMHandler(elementClass, columnCount, defaultValue)
}

class ArrayOfNRowLayout<E:Any> (private val elementClass: Class<E>,
                                private val columnCount: Int) : RowLayout<Array<out E?>, E?>() {
    override fun makeRowHandler(): RowHandler<Array<out E?>, E?> =
            RowArrayNHandler(elementClass, columnCount)
}

class FieldByPositionRowLayout<F:Any> (val fieldClass: Class<F>, val position: Int) : RowLayout<F, F>() {
    override fun makeRowHandler(): RowHandler<F, F> {
        TODO("not implemented yet")
    }
}

class FieldByNameRowLayout<F:Any> (val fieldClass: Class<F>, val name: String) : RowLayout<F, F>() {
    override fun makeRowHandler(): RowHandler<F, F> {
        TODO("not implemented yet")
    }
}



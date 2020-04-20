package org.jetbrains.dekaf.main.queries


fun<R:Any, B> layTableListOf(rowLayout: RowLayout<R,B>): QueryLayout<List<R>> =
        QueryTableLayout(ListTableLayout<R,B>(), rowLayout)

fun<R:Any, B> layTableArrayOf(rowLayout: RowLayout<R,B>): QueryLayout<Array<R>> =
        QueryTableLayout(ArrayTableLayout<R,B>(), rowLayout)



inline fun<reified E:Any> layRowArrayOf(columnCount: Int, defaultValue: E): RowLayout<Array<out E>, E> =
        layRowArrayOf(E::class.java, columnCount, defaultValue)

fun<E:Any> layRowArrayOf(elementClass: Class<E>, columnCount: Int, defaultValue: E): RowLayout<Array<out E>, E> =
        ArrayOfMRowLayout(elementClass, columnCount, defaultValue)


inline fun<reified E:Any> layRowArrayOf(columnCount: Int = 0): RowLayout<Array<out E?>, E?> =
        layRowArrayOf(E::class.java, columnCount)

fun<E:Any> layRowArrayOf(elementClass: Class<E>, columnCount: Int = 0): RowLayout<Array<out E?>, E?> =
        ArrayOfNRowLayout(elementClass, columnCount)

















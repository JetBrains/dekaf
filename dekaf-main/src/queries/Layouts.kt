package org.jetbrains.dekaf.main.queries

import java.util.stream.Stream


fun<R:Any, B> layTableListOf(rowLayout: RowLayout<R,B>): QueryLayout<List<R>> =
        QueryTableLayout(ListTableLayout<R,B>(), rowLayout)

fun<R:Any, B> layTableArrayOf(rowLayout: RowLayout<R,B>): QueryLayout<Array<R>> =
        QueryTableLayout(ArrayTableLayout<R,B>(), rowLayout)

fun<R:Any, B> layTableStreamOf(rowLayout: RowLayout<R,B>): QueryLayout<Stream<R>> =
        QueryTableLayout(StreamTableLayout<R,B>(), rowLayout)

fun<R:Any, B> layTableIteratorOf(rowLayout: RowLayout<R,B>): QueryLayout<Iterator<R>> =
        QueryTableLayout(IterateTableLayout<R,B>(), rowLayout)


inline fun<reified R, reified C1:Any, reified C2:Any> layRow(noinline function: (C1?,C2?) -> R): RowLayout<R, Any?> =
        layRow(R::class.java, C1::class.java, C2::class.java, function)

fun<R, C1:Any, C2:Any> layRow(rowClass: Class<R>, class1: Class<C1>, class2: Class<C2>, function: (C1?,C2?) -> R): RowLayout<R, Any?> =
        Fun2RowLayout(rowClass, class1, class2, function)


inline fun<reified R, reified C1:Any, reified C2:Any, reified C3:Any> layRow(noinline function: (C1?,C2?,C3?) -> R): RowLayout<R, Any?> =
        layRow(R::class.java, C1::class.java, C2::class.java, C3::class.java, function)

fun<R, C1:Any, C2:Any, C3:Any> layRow(rowClass: Class<R>, class1: Class<C1>, class2: Class<C2>, class3: Class<C3>, function: (C1?,C2?,C3?) -> R): RowLayout<R, Any?> =
        Fun3RowLayout(rowClass, class1, class2, class3, function)


inline fun<reified E:Any> layRowArrayOf(columnCount: Int, defaultValue: E): RowLayout<Array<out E>, E> =
        layRowArrayOf(E::class.java, columnCount, defaultValue)

fun<E:Any> layRowArrayOf(elementClass: Class<E>, columnCount: Int, defaultValue: E): RowLayout<Array<out E>, E> =
        ArrayOfMRowLayout(elementClass, columnCount, defaultValue)


inline fun<reified E:Any> layRowArrayOf(columnCount: Int = 0): RowLayout<Array<out E?>, E?> =
        layRowArrayOf(E::class.java, columnCount)

fun<E:Any> layRowArrayOf(elementClass: Class<E>, columnCount: Int = 0): RowLayout<Array<out E?>, E?> =
        ArrayOfNRowLayout(elementClass, columnCount)

















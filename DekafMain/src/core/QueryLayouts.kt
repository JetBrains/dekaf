@file:JvmName("QueryLayouts")
package org.jetbrains.dekaf.core



/// RESULT LAYOUT \\\

fun layoutExistence(): QueryResultLayout<Boolean> = QueryResultExistenceLayout()

fun<V> layoutSingleValueOf(valueClass: Class<V>): QueryResultLayout<V> = QueryResultOneRowLayout(rowValueOf(valueClass))
inline fun<reified V> layoutSingleValueOf(): QueryResultLayout<V> = layoutSingleValueOf(V::class.java)

fun layoutArrayOfShort(): QueryResultLayout<ShortArray> = QueryResultArrayOfShortLayout()

fun layoutArrayOfInt(): QueryResultLayout<IntArray> = QueryResultArrayOfIntLayout()

fun layoutArrayOfLong(): QueryResultLayout<LongArray> = QueryResultArrayOfLongLayout()

fun<R> layoutArrayOf(row: QueryRowLayout<R>): QueryResultLayout<Array<out R>> = QueryResultArrayLayout(row)

fun<R> layoutListOf(row: QueryRowLayout<R>): QueryResultLayout<List<R>> = QueryResultListLayout(row)

fun<R> layoutSetOf(row: QueryRowLayout<R>): QueryResultLayout<Set<R>> = QueryResultSetLayout(row)



/// ROW LAYOUTS \\\

fun<V> rowValueOf(valueClass: Class<V>): QueryRowLayout<V> = QueryRowOneValueLayout(valueClass)
inline fun<reified V> rowValueOf(): QueryRowLayout<V> = rowValueOf(V::class.java)

fun<R> rowStructOf(cortegeClass: Class<R>): QueryRowLayout<R> = QueryRowStructLayout(cortegeClass)
inline fun<reified R> rowStructOf(): QueryRowLayout<R> = rowStructOf(R::class.java)



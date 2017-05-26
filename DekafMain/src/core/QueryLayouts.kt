//@file:JvmName("QueryLayouts")
package org.jetbrains.dekaf.core



object QueryLayouts {

    /// RESULT LAYOUT \\\

    @JvmStatic
    fun existence(): QueryResultLayout<Boolean> = QueryResultExistenceLayout()

    @JvmStatic
    fun<R> arrayOf(row: QueryRowLayout<R>): QueryResultLayout<Array<out R>> = QueryResultArrayLayout(row)

    @JvmStatic
    fun<R> listOf(row: QueryRowLayout<R>): QueryResultLayout<List<R>> = QueryResultListLayout(row)

    


    /// ROW LAYOUTS \\\

    @JvmStatic
    fun<V> valueOf(valueClass: Class<V>): QueryRowLayout<V> = QueryRowOneValueLayout(valueClass)
    inline fun<reified V> valueOf(): QueryRowLayout<V> = valueOf(V::class.java)

    @JvmStatic
    fun<R> structOf(cortegeClass: Class<R>): QueryRowLayout<R> = QueryRowStructLayout(cortegeClass)
    inline fun<reified R> structOf(): QueryRowLayout<R> = structOf(R::class.java)



}

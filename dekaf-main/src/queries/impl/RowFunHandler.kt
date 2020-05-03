package org.jetbrains.dekaf.main.queries.impl

import org.jetbrains.dekaf.inter.intf.InterMatrixCursor
import org.jetbrains.dekaf.inter.intf.InterSeance
import org.jetbrains.dekaf.main.util.guessCommonOf
import org.jetbrains.dekaf.main.util.isInstanceOf
import org.jetbrains.dekaf.main.util.limitToOneString


sealed class RowFunHandler<R> : RowHandler<R, Any?> {

    final override val rowClass: Class<R>

    protected lateinit var cursor: InterMatrixCursor<Any?>

    constructor(rowClass: Class<R>) {
        this.rowClass = rowClass
    }

}


class RowFun2Handler<R, C1, C2> : RowFunHandler<R> {

    private val class1: Class<out C1>
    private val class2: Class<out C2>
    private val baseClass: Class<out Any>
    private val function: (C1?,C2?) -> R

    constructor(rowClass: Class<R>,
                class1: Class<C1>,
                class2: Class<C2>,
                function: (C1?,C2?) -> R)
        : super(rowClass)
    {
        this.class1 = class1
        this.class2 = class2
        baseClass = guessCommonOf(class1, class2)
        this.function = function
    }


    override fun prepare(seance: InterSeance, position: Int): InterMatrixCursor<Any?> {
        val cursor: InterMatrixCursor<Any?> = seance.makeMatrixCursor(position, baseClass)
        cursor.prepare(arrayOf<Class<out Any?>>(class1, class2))
        this.cursor = cursor
        return cursor
    }

    override fun handleRow(values: Array<out Any?>): R {
        val v1: C1? = castArgumentTo(class1, values[0], 1)
        val v2: C2? = castArgumentTo(class2, values[1], 2)
        return function(v1, v2)
    }

}


class RowFun3Handler<R, C1:Any, C2:Any, C3:Any> : RowFunHandler<R> {

    private val class1: Class<C1>
    private val class2: Class<C2>
    private val class3: Class<C3>
    private val baseClass: Class<out Any>
    private val function: (C1?,C2?,C3?) -> R

    constructor(rowClass: Class<R>,
                class1: Class<C1>,
                class2: Class<C2>,
                class3: Class<C3>,
                function: (C1?,C2?,C3?) -> R)
        : super(rowClass)
    {
        this.class1 = class1
        this.class2 = class2
        this.class3 = class3
        baseClass = guessCommonOf(class1, class2, class3)
        this.function = function
    }


    override fun prepare(seance: InterSeance, position: Int): InterMatrixCursor<Any?> {
        val cursor = seance.makeMatrixCursor(position, baseClass)
        cursor.prepare(arrayOf<Class<out Any?>>(class1, class2, class3))
        this.cursor = cursor
        return cursor
    }

    override fun handleRow(values: Array<out Any?>): R {
        val v1: C1? = castArgumentTo(class1, values[0], 1)
        val v2: C2? = castArgumentTo(class2, values[1], 2)
        val v3: C3? = castArgumentTo(class3, values[2], 3)
        return function(v1, v2, v3)
    }

}


private fun<V> castArgumentTo(clazz: Class<out V>, value: Any?, argumentIndex: Int): V? {
    if (value == null) return null
    if (value isInstanceOf clazz) {
        @Suppress("unchecked_cast")
        return value as V
    }

    val valueStr = value.toString().limitToOneString()
    val message = "Failed to case argument $argumentIndex to required class ${clazz.simpleName}: " +
                  "the actual class is: ${value.javaClass.simpleName}, the actual value is: $valueStr"
    throw ClassCastException(message)
}
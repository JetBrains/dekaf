package org.jetbrains.dekaf.main.queries.impl

import org.jetbrains.dekaf.inter.intf.InterMatrixCursor
import org.jetbrains.dekaf.inter.intf.InterSeance


interface RowHandler<R, B> {

    fun prepare(seance: InterSeance, position: Int): InterMatrixCursor<B>

    fun handleRow(values: Array<out B>): R

    val rowClass: Class<R>

}
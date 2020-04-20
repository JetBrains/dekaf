package org.jetbrains.dekaf.main.queries.impl

import org.jetbrains.dekaf.inter.intf.InterSeance


interface ResultCollector<T> : AutoCloseable {

    fun prepare(seance: InterSeance, positionIndex: Int)

    fun retrieve(): T

    override fun close()

}

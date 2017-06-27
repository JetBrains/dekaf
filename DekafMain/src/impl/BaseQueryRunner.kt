package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.core.DBQueryRunner
import org.jetbrains.dekaf.core.QueryResultLayout
import org.jetbrains.dekaf.core.TaskKind
import org.jetbrains.dekaf.inter.InterCursor
import org.jetbrains.dekaf.inter.InterSeance
import org.jetbrains.dekaf.inter.InterTask


internal class BaseQueryRunner<T>: BaseRunner, DBQueryRunner<T> {

    private val layout: QueryResultLayout<T>
    private var portionSize: Int = 100

    private var interCursor: InterCursor? = null


    internal constructor(session: BaseSession, interSeance: InterSeance, text: String, layout: QueryResultLayout<T>)
            : super(session, interSeance, text)
    {
        this.layout = layout
    }

    override fun prepare() {
        val task = InterTask(TaskKind.TASK_QUERY, text)
        interSeance.prepare(task)
    }

    override fun withParams(vararg params: Any?): BaseQueryRunner<T> {
        if (params.isNotEmpty()) {
            interSeance.assignParameters(params)
        }
        return this
    }

    override fun packBy(rowsPerPack: Int): DBQueryRunner<T> {
        this.portionSize = rowsPerPack
        return this
    }

    override fun nextPack(): T? {
        ensureOpenedCursor()
        val cursor = interCursor ?: return null

        val a = cursor.retrievePortion() ?: return null

        val builder = layout.makeBuilder()
        builder.add(a)
        return builder.build()
    }

    override fun run(): T? {
        interSeance.execute()
        ensureOpenedCursor()
        val cursor = interCursor ?: return null
        val builder = layout.makeBuilder()
        var portion: Any? = cursor.retrievePortion()
        while (portion != null) {
            builder.add(portion)
            portion = cursor.retrievePortion()
        }
        return builder.build()
    }

    private fun ensureOpenedCursor() {
        if (interCursor == null) {
            val interLayout = layout.makeInterLayout()
            interCursor = interSeance.openCursor(0.toByte(), interLayout)
            if (interCursor != null) {
                interCursor!!.setPortionSize(portionSize)
            }
        }
    }

    override fun <I : Any?> getSpecificService(serviceClass: Class<I>, serviceName: String): I? {
        return interSeance.getSpecificService(serviceClass, serviceName)
    }
}
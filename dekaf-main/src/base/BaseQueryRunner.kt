package org.jetbrains.dekaf.main.base

import org.jetbrains.dekaf.inter.common.StatementCategory
import org.jetbrains.dekaf.inter.intf.InterSeance
import org.jetbrains.dekaf.main.db.DbQueryRunner
import org.jetbrains.dekaf.main.queries.QueryLayout
import org.jetbrains.dekaf.main.queries.impl.ResultCollector
import java.util.*


class BaseQueryRunner<T> : DbQueryRunner<T> {

    private val session: BaseSession
    private val seance: InterSeance
    private val queryText: String
    private val layout: QueryLayout<T>



    constructor(session: BaseSession, seance: InterSeance, queryText: String, layout: QueryLayout<T>) {
        this.session = session
        this.seance = seance
        this.queryText = queryText
        this.layout = layout
    }

    
    override fun run(vararg paramValues: Any?): T {
        val params: List<Any?>? = if (paramValues.isNotEmpty()) Arrays.asList(*paramValues) else null
        seance.prepare(queryText, StatementCategory.stmtQuery, null)
        seance.execute(params)
        val collector: ResultCollector<T> = layout.makeResultCollector()
        collector.prepare(seance, 0)
        val result: T = collector.retrieve()
        collector.close()
        return result
    }

    override fun close() {
        //TODO("not implemented yet")
    }

}
package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.core.DBCommandRunner
import org.jetbrains.dekaf.core.TaskKind.TASK_COMMAND
import org.jetbrains.dekaf.inter.InterSeance
import org.jetbrains.dekaf.inter.InterTask


internal class BaseCommandRunner: BaseStatementRunner, DBCommandRunner {

    internal constructor(session: BaseSession, interSeance: InterSeance, text: String) : super(session, interSeance, text)

    override fun prepare() {
        val task = InterTask(TASK_COMMAND, text)
        interSeance.prepare(task)
        prepared = true
    }

    override fun withParams(vararg params: Any?): BaseCommandRunner {
        super.withParams(*params)
        return this
    }

    override fun run(): BaseCommandRunner {
        if (!prepared) prepare()
        interSeance.execute()
        return this
    }

    override val affectedRowsCount: Int
        get() = interSeance.affectedRowsCount
}
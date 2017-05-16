package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.core.DBCommandRunner
import org.jetbrains.dekaf.core.TaskKind.TASK_COMMAND
import org.jetbrains.dekaf.inter.InterSeance
import org.jetbrains.dekaf.inter.InterTask


internal class BaseCommandRunner: BaseRunner, DBCommandRunner {

    constructor(session: BaseSession, inter: InterSeance, text: String) : super(session, inter, text)

    override fun prepare() {
        val task = InterTask(TASK_COMMAND, text)
        inter.prepare(task)
    }

    override fun withParams(vararg params: Any?): BaseCommandRunner {
        super.withParams(*params)
        return this
    }

    override fun run(): BaseCommandRunner {
        inter.execute()
        return this
    }

    override val affectedRowsCount: Int
        get() = inter.affectedRowsCount
}
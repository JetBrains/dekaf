package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.inter.InterSeance


internal abstract class BaseRunner {

    protected val session: BaseSession

    constructor(session: BaseSession) {
        this.session = session
    }

    abstract fun close()
}


internal abstract class BaseStatementRunner : BaseRunner {

    protected val interSeance: InterSeance

    protected val text: String

    protected var prepared = false

    constructor(session: BaseSession, interSeance: InterSeance, text: String)
            : super(session)
    {
        this.interSeance = interSeance
        this.text = text
    }


    abstract fun prepare()
    
    open fun withParams(vararg params: Any?): BaseRunner {
        if (!prepared) {
            prepare()
        }
        if (params.isNotEmpty()) {
            interSeance.assignParameters(params)
        }
        return this
    }

    abstract fun run(): Any?

    override fun close() {
        prepared = false
        interSeance.close()
        session.runnerClosed(this)
    }

}
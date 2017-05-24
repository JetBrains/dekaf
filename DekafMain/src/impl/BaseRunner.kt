package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.inter.InterSeance


internal abstract class BaseRunner {

    val session: BaseSession

    val interSeance: InterSeance

    val text: String


    constructor(session: BaseSession, inter: InterSeance, text: String) {
        this.session = session
        this.interSeance = inter
        this.text = text
    }

    abstract fun prepare()

    open fun withParams(vararg params: Any?): BaseRunner {
        interSeance.assignParameters(params)
        return this
    }

    fun close() {
        interSeance.close()
        session.runnerClosed(this)
    }
}
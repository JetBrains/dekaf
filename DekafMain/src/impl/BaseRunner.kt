package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.inter.InterSeance


internal abstract class BaseRunner {

    val session: BaseSession

    val inter: InterSeance

    val text: String


    constructor(session: BaseSession, inter: InterSeance, text: String) {
        this.session = session
        this.inter = inter
        this.text = text
    }

    abstract fun prepare()

    open fun withParams(vararg params: Any?): BaseRunner {
        inter.assignParameters(params)
        return this
    }

    fun close() {
        inter.close()
        session.runnerClosed(this)
    }
}
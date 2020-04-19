package org.jetbrains.dekaf.main.base

import org.jetbrains.dekaf.inter.intf.InterSession
import org.jetbrains.dekaf.main.db.DbSession
import org.jetbrains.dekaf.main.util.choose


class BaseSession (private val facade: BaseFacade) : DbSession {

    private var interSession: InterSession? = null

    private var broken: Boolean = false
    private var closed: Boolean = false


    constructor(facade: BaseFacade, interSession: InterSession) : this(facade) {
        this.interSession = interSession
    }


    override fun ping() {
        ensureInterSession().ping()
    }


    private fun ensureInterSession(): InterSession {
        val s = interSession
        if (closed || s == null || s.isClosed)
            throw IllegalStateException("DB session is " + broken.choose("broken and closed", "closed"))
        return s
    }


    override fun markBroken() {
        broken = true
        val s = detachInterSession()
        if (s != null) facade.releaseInterSessionBroken(s)
    }

    override fun close() {
        val s = detachInterSession()
        if (s != null) facade.releaseInterSessionBack(s)
    }

    private fun detachInterSession(): InterSession? {
        val s = interSession
        interSession = null
        closed = true
        return s
    }

}
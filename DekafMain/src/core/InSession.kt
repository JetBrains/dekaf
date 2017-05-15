package org.jetbrains.dekaf.core


/**
 * In-session closure.
 */
interface InSession<out R> {

    fun run(session: DBSession): R

}

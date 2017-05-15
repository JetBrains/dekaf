package org.jetbrains.dekaf.core


/**
 * In-session closure.
 */
interface InSessionNoResult {

    fun run(session: DBSession)

}

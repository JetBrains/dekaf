package org.jetbrains.dekaf.core


/**
 * In-transaction closure.
 */
interface InTransaction<out R> {

    fun run(tran: DBTransaction): R

}

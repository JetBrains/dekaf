package org.jetbrains.dekaf.core


/**
 * In-transaction closure.
 */
interface InTransactionNoResult {

    fun run(tran: DBTransaction)
    
}

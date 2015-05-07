package org.jetbrains.jdba.core1;

import org.jetbrains.annotations.NotNull;



/**
 * In-transaction closure.
 */
public interface InTransactionNoResult {

  abstract void run(@NotNull final DBTransaction tran);
}

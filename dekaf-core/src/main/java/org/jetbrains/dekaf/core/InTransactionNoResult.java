package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;



/**
 * In-transaction closure.
 */
public interface InTransactionNoResult {

  void run(@NotNull final DBTransaction tran);
}

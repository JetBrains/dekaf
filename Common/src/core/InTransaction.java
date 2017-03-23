package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;



/**
 * In-transaction closure.
 */
public interface InTransaction<R> {

  R run(@NotNull final DBTransaction tran);
}

package org.jetbrains.dba.base;

import org.jetbrains.annotations.NotNull;



/**
 * In-transaction closure.
 */
public interface InTransaction<R> {

  abstract R run(@NotNull final DBTransaction tran);
}

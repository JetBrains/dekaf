package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;



/**
 * In-transaction closure.
 */
public interface InTransaction<R> {

  abstract R run(@NotNull final DBTransaction tran);
}

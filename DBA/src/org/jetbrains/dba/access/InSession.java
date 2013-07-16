package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;



/**
 * In-session closure.
 */
public interface InSession<R> {

  abstract R run(@NotNull final DBSession session);
}

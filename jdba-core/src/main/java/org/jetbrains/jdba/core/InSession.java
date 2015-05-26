package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;



/**
 * In-session closure.
 */
public interface InSession<R> {

  R run(@NotNull final DBSession session);
}

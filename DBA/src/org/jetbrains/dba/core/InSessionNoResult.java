package org.jetbrains.dba.core;

import org.jetbrains.annotations.NotNull;



/**
 * In-session closure.
 */
public interface InSessionNoResult {

  abstract void run(@NotNull final DBSession session);
}

package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;



/**
 * In-session closure.
 */
public interface InSessionNoResult {

  void run(@NotNull final DBSession session);
}

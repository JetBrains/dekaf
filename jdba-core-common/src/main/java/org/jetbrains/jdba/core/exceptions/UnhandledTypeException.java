package org.jetbrains.jdba.core.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 *
 **/
public class UnhandledTypeException extends DBException {
  public UnhandledTypeException(@NotNull final String message, @Nullable String statementText) {
    super(message, statementText);
  }
}

package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class UnexpectedReflectionException extends RuntimeException {

  public UnexpectedReflectionException(@NotNull final String message, @NotNull final Exception e) {
    super(message + ": " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
  }

}

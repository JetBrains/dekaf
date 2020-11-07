package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class StrippedUnknownException extends Exception {

  @NotNull
  public final String originalClassName;

  public StrippedUnknownException(@NotNull final Throwable originalException,
                                  @Nullable final Throwable cause) {
    super(prepareMessage(originalException), cause);
    originalClassName = originalException.getClass().getName();
    setStackTrace(originalException.getStackTrace());
  }

  private static String prepareMessage(@NotNull final Throwable originalException) {
    return originalException.getClass().getName() + ": " + originalException.getMessage();
  }

}

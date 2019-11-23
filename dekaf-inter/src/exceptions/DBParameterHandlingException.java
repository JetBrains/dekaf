package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class DBParameterHandlingException extends DBException {

  public DBParameterHandlingException(@NotNull final String message,
                                      @NotNull final Exception exception,
                                      final String statementText) {
    super(message, exception, statementText);
  }

}

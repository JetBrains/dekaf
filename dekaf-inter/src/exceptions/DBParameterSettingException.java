package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBParameterSettingException extends DBParameterHandlingException {

  public DBParameterSettingException(@NotNull final String message,
                                     @NotNull final Exception exception,
                                     final String statementText) {
    super(message, exception, statementText);
  }

}

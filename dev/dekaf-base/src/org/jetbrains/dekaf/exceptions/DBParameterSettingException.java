package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBParameterSettingException extends DBParameterHandlingException {

  public DBParameterSettingException(final @NotNull String message,
                                     final @NotNull Exception exception,
                                     final String statementText) {
    super(message, exception, statementText);
  }

  public DBParameterSettingException(final @NotNull String message) {
    super(message);
  }

}

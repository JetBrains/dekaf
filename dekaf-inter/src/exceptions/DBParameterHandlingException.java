package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class DBParameterHandlingException extends DBException {

  protected DBParameterHandlingException(final @NotNull String message,
                                         final @NotNull SQLException sqlException,
                                         final @Nullable String statementText) {
    super(message, sqlException, statementText);
  }

  public DBParameterHandlingException(final @NotNull String message,
                                      final @NotNull Exception exception,
                                      final @NotNull String statementText) {
    super(message, exception, statementText);
  }

}

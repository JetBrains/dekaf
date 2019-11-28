package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBParameterSettingException extends DBParameterHandlingException {

  public DBParameterSettingException(final @NotNull String message,
                                     final @NotNull SQLException sqlException,
                                     final @Nullable String statementText) {
    super(message, sqlException, statementText);
  }

  public DBParameterSettingException(final @NotNull String message,
                                     final @NotNull Exception exception,
                                     final @Nullable String statementText) {
    super(message, exception, statementText);
  }

}

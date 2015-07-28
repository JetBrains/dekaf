package org.jetbrains.jdba.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class DBProtectionException extends DBException {

  public DBProtectionException(@NotNull final String message,
                               @Nullable final String statementText) {
    super(message, statementText);
  }

}

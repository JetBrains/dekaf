package org.jetbrains.jdba.jdbc.pooling;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.errors.DBError;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class ConnectionPoolException extends DBError {

  public ConnectionPoolException(@NotNull String message, @NotNull Throwable cause, @Nullable String statementText) {
    super(message, cause, statementText);
  }


  public ConnectionPoolException(@NotNull String message, @Nullable String statementText) {
    super(message, statementText);
  }

}

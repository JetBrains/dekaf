package org.jetbrains.dekaf.jdbc.pooling;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class ConnectionPoolException extends DBException {

  public ConnectionPoolException(@NotNull String message, @NotNull Throwable cause, @Nullable String statementText) {
    super(message, cause, statementText);
  }


  public ConnectionPoolException(@NotNull String message, @Nullable String statementText) {
    super(message, statementText);
  }

}

package org.jetbrains.dekaf.jdbc.pooling;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class ConnectionPoolOperationInterruptedException extends ConnectionPoolException {

  public ConnectionPoolOperationInterruptedException(@NotNull String message, @NotNull Throwable cause, @Nullable String statementText) {
    super(message, cause, statementText);
  }

}

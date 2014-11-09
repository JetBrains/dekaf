package org.jetbrains.dba.pooling;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class ConnectionPoolExhaustedException extends ConnectionPoolException {
  public ConnectionPoolExhaustedException(@NotNull String message) {
    super(message, null);
  }
}

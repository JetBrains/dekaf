package org.jetbrains.dba.jdbc.pooling;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class ConnectionPoolExhaustedException extends ConnectionPoolException {
  public ConnectionPoolExhaustedException(@NotNull String message) {
    super(message, null);
  }
}

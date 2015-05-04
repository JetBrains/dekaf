package org.jetbrains.jdba.jdbc.pooling;

/**
 * @author Leonid Bushuev from JetBrains
 */
public class ConnectionPoolIsNotReadyException extends ConnectionPoolException {

  public ConnectionPoolIsNotReadyException(String message) {
    super(message, null);
  }

}

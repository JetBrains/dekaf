package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBTransactionIsAlreadyStartedException extends DBTransactionException {

  public DBTransactionIsAlreadyStartedException() {
    super("The session is already inside a transaction", null);
  }

  public DBTransactionIsAlreadyStartedException(@NotNull final String message) {
    super(message, null);
  }

}

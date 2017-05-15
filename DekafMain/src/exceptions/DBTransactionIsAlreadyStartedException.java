package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBTransactionIsAlreadyStartedException extends DBException {

  public DBTransactionIsAlreadyStartedException() {
    super("Transaction is already started.", null);
  }

  public DBTransactionIsAlreadyStartedException(@NotNull final String message) {
    super(message, null);
  }

}

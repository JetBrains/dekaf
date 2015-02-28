package org.jetbrains.dba.fakedb.errors;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.errors.DBError;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class FakeSessionBusyException extends DBError {

  public FakeSessionBusyException(String message, Throwable cause, @Nullable String statementText) {
    super(message, cause, statementText);
  }
}

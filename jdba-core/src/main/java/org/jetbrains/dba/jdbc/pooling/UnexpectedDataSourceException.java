package org.jetbrains.dba.jdbc.pooling;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class UnexpectedDataSourceException extends ConnectionPoolException {

  public UnexpectedDataSourceException(@NotNull String message, @Nullable String statementText) {
    super(message, statementText);
  }

}

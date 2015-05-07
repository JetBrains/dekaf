package org.jetbrains.jdba.core.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * @author Leonid Bushuev from JetBrains
 */

public class DbmsUnsupportedFeatureException extends DBException {

  public DbmsUnsupportedFeatureException(@NotNull final String message,
                                         @Nullable String statementText) {
    super(message, statementText);
  }
}

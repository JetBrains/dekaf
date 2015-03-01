package org.jetbrains.dba.core.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * @author Leonid Bushuev from JetBrains
 */

public class DbmsUnsupportedFeatureError extends DBError {

  public DbmsUnsupportedFeatureError(@NotNull final String message, @Nullable String statementText) {
    super(message, statementText);
  }
}

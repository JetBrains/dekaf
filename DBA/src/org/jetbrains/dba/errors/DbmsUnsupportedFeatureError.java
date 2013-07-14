package org.jetbrains.dba.errors;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */

public class DbmsUnsupportedFeatureError extends DBError {

  public DbmsUnsupportedFeatureError(@NotNull final String message) {
    super(message);
  }
}

package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.errors.DBError;
import org.jetbrains.dba.errors.UnknownDBError;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
final class UnknownErrorRecognizer extends DatabaseAbstractErrorRecognizer {

  @NotNull
  @Override
  protected DBError recognizeSpecificError(@NotNull final SQLException sqlException) {
    return new UnknownDBError(sqlException);
  }

}

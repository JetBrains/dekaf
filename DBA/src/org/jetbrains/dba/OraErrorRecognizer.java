package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.errors.DBError;
import org.jetbrains.dba.errors.DuplicateKeyError;
import org.jetbrains.dba.errors.UnknownDBError;

import java.sql.SQLException;



/**
 *
 **/
public class OraErrorRecognizer extends DatabaseAbstractErrorRecognizer {

  @NotNull
  @Override
  protected DBError recognizeSpecificError(@NotNull final SQLException sqlException) {
    int code = sqlException.getErrorCode();

    switch (code) {
      case 1:
        return new DuplicateKeyError(sqlException);
      default:
        return new UnknownDBError(sqlException);
    }
  }
}

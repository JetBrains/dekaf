package org.jetbrains.dba.rdbms.microsoft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.core.BaseErrorRecognizer;
import org.jetbrains.dba.core.errors.DBError;
import org.jetbrains.dba.core.errors.DuplicateKeyError;
import org.jetbrains.dba.core.errors.UnknownDBError;

import java.sql.SQLException;



/**
 * MS SQL Server specific errors recognizer.
 **/
public class MssqlErrorRecognizer extends BaseErrorRecognizer {

  @NotNull
  @Override
  protected DBError recognizeSpecificError(@NotNull final SQLException sqlException, @Nullable final String statementText) {
    int code = sqlException.getErrorCode();

    switch (code) {
      case 2601:
        return new DuplicateKeyError(sqlException, statementText);
      default:
        return new UnknownDBError(sqlException, statementText);
    }
  }
}

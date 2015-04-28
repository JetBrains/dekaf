package org.jetbrains.jdba.microsoft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.exceptions.DBException;
import org.jetbrains.jdba.core.exceptions.DuplicateKeyException;
import org.jetbrains.jdba.core.exceptions.UnknownDBException;
import org.jetbrains.jdba.jdbc.BaseErrorRecognizer;

import java.sql.SQLException;



/**
 * MS SQL Server specific errors recognizer.
 **/
public class MssqlErrorRecognizer extends BaseErrorRecognizer {

  @NotNull
  @Override
  protected DBException recognizeSpecificError(@NotNull final SQLException sqlException, @Nullable final String statementText) {
    int code = sqlException.getErrorCode();

    switch (code) {
      case 2601:
        return new DuplicateKeyException(sqlException, statementText);
      default:
        return new UnknownDBException(sqlException, statementText);
    }
  }
}

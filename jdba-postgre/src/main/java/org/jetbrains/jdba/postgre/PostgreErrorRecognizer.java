package org.jetbrains.jdba.postgre;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.exceptions.DBException;
import org.jetbrains.jdba.exceptions.DuplicateKeyException;
import org.jetbrains.jdba.exceptions.UnknownDBException;
import org.jetbrains.jdba.jdbc.BaseErrorRecognizer;

import java.sql.SQLException;



/**
 * PostgreSQL specific errors recognizer.
 **/
public class PostgreErrorRecognizer extends BaseErrorRecognizer {

  @NotNull
  @Override
  protected DBException recognizeSpecificError(@NotNull final SQLException sqlException, @Nullable final String statementText) {
    int code = sqlException.getErrorCode();

    switch (code) {
      case 1:
        return new DuplicateKeyException(sqlException, statementText);
      default:
        return new UnknownDBException(sqlException, statementText);
    }
  }
}

package org.jetbrains.jdba.oracle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.exceptions.DBException;
import org.jetbrains.jdba.core.exceptions.DuplicateKeyException;
import org.jetbrains.jdba.core.exceptions.UnknownDBException;
import org.jetbrains.jdba.jdbc.BaseErrorRecognizer;

import java.sql.SQLException;
import java.util.regex.Pattern;



/**
 * Oracle specific errors recognizer.
 **/
public class OraErrorRecognizer extends BaseErrorRecognizer {

  @NotNull
  @Override
  protected DBException recognizeSpecificError(@NotNull final SQLException sqlException, @Nullable final String statementText) {
    int code = sqlException.getErrorCode();

    switch (code) {
      case 1:
        return new DuplicateKeyException(sqlException, statementText);
      default:
        String msg = sqlException.getMessage().trim();
        boolean hasNumber = NUMBERED_ERROR_PATTERN.matcher(msg).matches();
        if (!hasNumber) msg = "Oracle SQL error " + code + ": " + msg;
        return new UnknownDBException(msg, sqlException, statementText);
    }
  }

  private static final Pattern NUMBERED_ERROR_PATTERN =
    Pattern.compile("^\\w{3}-\\d+:.*$");

}

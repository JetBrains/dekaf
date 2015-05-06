package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.exceptions.DBException;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class PostgreErrorRecognizer extends BaseErrorRecognizer {

  public static final PostgreErrorRecognizer INSTANCE = new PostgreErrorRecognizer();


  @NotNull
  @Override
  protected DBException recognizeSpecificError(@NotNull final SQLException sqlException,
                                               @Nullable final String statementText) {
    return super.recognizeSpecificError(sqlException, statementText);
  }

}

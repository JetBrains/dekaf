package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.CallableStatement;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseCommandRunner implements DBCommandRunner {
  @NotNull
  protected final BaseSession session;


  @NotNull
  protected final String statementSourceText;


  @Nullable
  protected Object[] params;


  public BaseCommandRunner(@NotNull final BaseSession session,
                           @NotNull final String statementSourceText) {
    this.session = session;
    this.statementSourceText = statementSourceText;
  }


  @Override
  public BaseCommandRunner withParams(final Object... params) {
    this.params = params;
    return this;
  }


  @Override
  public BaseCommandRunner run() {
    try {
      final CallableStatement statement = session.prepareCall(statementSourceText);
      try {
        if (params != null) {
          session.assignParameters(statement, params);
        }
        statement.execute();
      }
      finally {
        statement.close();
      }
    }
    catch (SQLException e) {
      throw session.recognizeError(e);
    }

    return this;
  }
}

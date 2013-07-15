package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.CallableStatement;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class CommandRunner {
  @NotNull
  protected final DatabaseAbstractSession session;


  @NotNull
  protected final String statementSourceText;


  @Nullable
  protected Object[] params;


  public CommandRunner(@NotNull final DatabaseAbstractSession session,
                       @NotNull final String statementSourceText) {
    this.session = session;
    this.statementSourceText = statementSourceText;
  }


  public CommandRunner withParams(final Object... params) {
    this.params = params;
    return this;
  }


  public CommandRunner run() {
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

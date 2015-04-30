package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.DBInterCursor;
import org.jetbrains.jdba.core.ParameterDef;
import org.jetbrains.jdba.core.ResultLayout;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcInterCallableStatementSeance extends JdbcInterSeance {


  @NotNull
  protected final ParameterDef[] myOutParameterDefs;



  protected JdbcInterCallableStatementSeance(@NotNull final JdbcInterSession session,
                                             @NotNull final String statementText,
                                             @NotNull final ParameterDef[] outParameterDefs) {
    super(session, statementText);
    this.myOutParameterDefs = outParameterDefs;

    try {
      CallableStatement stmt = session.getConnection().prepareCall(statementText);
      for (int i = 0, n = outParameterDefs.length; i < n; i++) {
        ParameterDef d = outParameterDefs[i];
        if (d == null) continue;

        // TODO register it correctly
        stmt.registerOutParameter(i+1, Types.VARCHAR);
      }
      statement = stmt;
    }
    catch (SQLException sqle) {
      throw session.recognizeException(sqle, statementText);
    }
  }

  @NotNull
  @Override
  public <R> DBInterCursor<R> openCursor(final int parameterPosition,
                                         @NotNull final ResultLayout<R> layout) {
    if (parameterPosition == 0) {
      return openDefaultCursor(layout);
    }
    else {
      return openCursorFromParameter(parameterPosition, layout);

    }
  }

  private <R> DBInterCursor<R> openCursorFromParameter(final int parameterPosition,
                                                       final ResultLayout<R> layout) {
    // TODO implement JdbcInterCallableStatementSeance.openCursor()
    throw new RuntimeException("Method JdbcInterCallableStatementSeance.openCursor() is not implemented yet.");
  }


  @Override
  public void close() {
    // TODO close all non-default cursors here

    super.close();
  }
}

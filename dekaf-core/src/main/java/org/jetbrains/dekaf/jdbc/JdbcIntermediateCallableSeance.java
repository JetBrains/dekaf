package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.ParameterDef;
import org.jetbrains.dekaf.core.ResultLayout;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateCursor;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcIntermediateCallableSeance extends JdbcIntermediateSeance {


  @NotNull
  protected final ParameterDef[] myOutParameterDefs;



  protected JdbcIntermediateCallableSeance(@NotNull final JdbcIntermediateSession session,
                                           @NotNull final String statementText,
                                           @NotNull final ParameterDef[] outParameterDefs) {
    super(session, statementText);
    this.myOutParameterDefs = outParameterDefs;

    try {
      CallableStatement stmt = session.prepareCallableStatement(statementText);
      for (int i = 0, n = outParameterDefs.length; i < n; i++) {
        ParameterDef d = outParameterDefs[i];
        if (d == null) continue;

        // TODO register it correctly
        stmt.registerOutParameter(i+1, Types.VARCHAR);
      }
      myStatement = stmt;
    }
    catch (SQLException sqle) {
      throw session.recognizeException(sqle, statementText);
    }
  }

  @NotNull
  @Override
  public <R> IntegralIntermediateCursor<R> openCursor(final int parameterPosition,
                                                      @NotNull final ResultLayout<R> layout) {
    if (parameterPosition == 0) {
      return openDefaultCursor(layout);
    }
    else {
      return openCursorFromParameter(parameterPosition, layout);
    }
  }

  private <R> IntegralIntermediateCursor<R> openCursorFromParameter(final int parameterPosition,
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

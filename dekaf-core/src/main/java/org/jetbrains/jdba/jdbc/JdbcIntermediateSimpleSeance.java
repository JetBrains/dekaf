package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.ResultLayout;
import org.jetbrains.jdba.intermediate.IntegralIntermediateCursor;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcIntermediateSimpleSeance extends JdbcIntermediateSeance {




  protected JdbcIntermediateSimpleSeance(@NotNull final JdbcIntermediateSession session,
                                         @NotNull final String statementText) {
    super(session, statementText);

    try {
      myStatement = session.prepareSimpleStatement(statementText);
    }
    catch (SQLException sqle) {
      throw session.recognizeException(sqle, statementText);
    }
  }


  @NotNull
  @Override
  public synchronized <R> IntegralIntermediateCursor<R> openCursor(final int parameterPosition,
                                                                   @NotNull final ResultLayout<R> layout) {
    if (parameterPosition != 0) {
      throw new IllegalStateException("SimpleSeance supports default (return) cursors only.");
    }

    return openDefaultCursor(layout);
  }
}

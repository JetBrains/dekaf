package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.ResultLayout;
import org.jetbrains.jdba.inter.DBInterCursor;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcInterSimpleSeance extends JdbcInterSeance {




  protected JdbcInterSimpleSeance(@NotNull final JdbcInterSession session,
                                  @NotNull final String statementText) {
    super(session, statementText);

    try {
      statement = session.getConnection().prepareCall(statementText);
    }
    catch (SQLException sqle) {
      throw session.recognizeException(sqle, statementText);
    }
  }


  @NotNull
  @Override
  public synchronized <R> DBInterCursor<R> openCursor(final int parameterPosition,
                                                      @NotNull final ResultLayout<R> layout) {
    if (parameterPosition != 0) {
      throw new IllegalStateException("SimpleSenace supports default (return) cursors only.");
    }

    return openDefaultCursor(layout);
  }
}

package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.DBInterCursor;
import org.jetbrains.jdba.core.ResultLayout;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcInterOtherCommandSeance extends JdbcInterSeance {

  protected JdbcInterOtherCommandSeance(@NotNull final JdbcInterSession session) {
    super(session);
  }

  @Override
  public void prepare(@NotNull final String queryText) {
    // TODO implement JdbcInterOtherCommandSeance.prepare()
    throw new RuntimeException("Method JdbcInterOtherCommandSeance.prepare() is not implemented yet.");

  }

  @Override
  public void setInParameters(@NotNull final Object[] parameters) {
    // TODO implement JdbcInterOtherCommandSeance.setInParameters()
    throw new RuntimeException("Method JdbcInterOtherCommandSeance.setInParameters() is not implemented yet.");

  }

  @NotNull
  @Override
  public <R> DBInterCursor<R> openCursor(final int parameterPosition,
                                         @NotNull final ResultLayout<R> layout) {
    // TODO implement JdbcInterOtherCommandSeance.openCursor()
    throw new RuntimeException("Method JdbcInterOtherCommandSeance.openCursor() is not implemented yet.");

  }

  @Override
  public void close() {
    // TODO implement JdbcInterOtherCommandSeance.close()
    throw new RuntimeException("Method JdbcInterOtherCommandSeance.close() is not implemented yet.");

  }
}

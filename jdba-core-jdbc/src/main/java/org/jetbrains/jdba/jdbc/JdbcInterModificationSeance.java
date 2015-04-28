package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.DBInterCursor;
import org.jetbrains.jdba.core.ResultLayout;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcInterModificationSeance extends JdbcInterSeance {

  protected JdbcInterModificationSeance(@NotNull final JdbcInterSession session) {
    super(session);
  }

  @Override
  public void prepare(@NotNull final String queryText) {
    // TODO implement JdbcInterModificationSeance.prepare()
    throw new RuntimeException("Method JdbcInterModificationSeance.prepare() is not implemented yet.");

  }

  @Override
  public void setInParameters(@NotNull final Object[] parameters) {
    // TODO implement JdbcInterModificationSeance.setInParameters()
    throw new RuntimeException("Method JdbcInterModificationSeance.setInParameters() is not implemented yet.");

  }

  @NotNull
  @Override
  public <R> DBInterCursor<R> openCursor(final int parameterPosition,
                                         @NotNull final ResultLayout<R> layout) {
    // TODO implement JdbcInterModificationSeance.openCursor()
    throw new RuntimeException("Method JdbcInterModificationSeance.openCursor() is not implemented yet.");

  }

  @Override
  public void close() {
    // TODO implement JdbcInterModificationSeance.close()
    throw new RuntimeException("Method JdbcInterModificationSeance.close() is not implemented yet.");

  }
}

package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.DBInterCursor;
import org.jetbrains.jdba.core.ResultLayout;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcInterQuerySeance extends JdbcInterSeance {


  protected JdbcInterQuerySeance(@NotNull final JdbcInterSession session) {
    super(session);
  }

  @Override
  public void prepare(@NotNull final String queryText) {
    // TODO implement JdbcInterQuerySeance.prepare()
    throw new RuntimeException("Method JdbcInterQuerySeance.prepare() is not implemented yet.");

  }

  @Override
  public void setInParameters(@NotNull final Object[] parameters) {
    // TODO implement JdbcInterQuerySeance.setInParameters()
    throw new RuntimeException("Method JdbcInterQuerySeance.setInParameters() is not implemented yet.");

  }

  @NotNull
  @Override
  public <R> DBInterCursor<R> openCursor(final int parameterPosition,
                                         @NotNull final ResultLayout<R> layout) {
    // TODO implement JdbcInterQuerySeance.openCursor()
    throw new RuntimeException("Method JdbcInterQuerySeance.openCursor() is not implemented yet.");

  }

  @Override
  public void close() {
    // TODO implement JdbcInterQuerySeance.close()
    throw new RuntimeException("Method JdbcInterQuerySeance.close() is not implemented yet.");

  }
}

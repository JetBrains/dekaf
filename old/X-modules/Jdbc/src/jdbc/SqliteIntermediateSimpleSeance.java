package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.ResultLayout;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateCursor;

public class SqliteIntermediateSimpleSeance extends JdbcIntermediateSimpleSeance {

  private static final IntegralIntermediateCursor EMPTY_CURSOR = new IntegralIntermediateCursor() {
    public boolean hasRows() { return false; }
    @NotNull
    public String[] getColumnNames() { return new String[0]; }
    public void setCollectLimit(int limit) { }
    public Object fetch() { return null; }
    public void close() { }
  };

  SqliteIntermediateSimpleSeance(@NotNull JdbcIntermediateSession session,
                                 @NotNull String statementText) {
    super(session, statementText);
  }

  @NotNull
  @Override
  @SuppressWarnings("unchecked")
  public synchronized <R> IntegralIntermediateCursor<R> openCursor(int parameterPosition, @NotNull ResultLayout<R> layout) {
    if (myDefaultResultSet == null) return EMPTY_CURSOR;
    return super.openCursor(parameterPosition, layout);
  }
}

package org.jetbrains.jdba.core1;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class BaseRowsCollector<S> implements DBRowsCollector<S> {

  @Nullable
  protected ColumnBriefInfo[] myColumns;



  protected boolean needMetaData() {
    return myColumns == null;
  }

  protected final void setupMetaData(@NotNull final ResultSetMetaData metaData) throws SQLException {
    int n = metaData.getColumnCount();
    ColumnBriefInfo[] columns = new ColumnBriefInfo[n];
    for (int i = 0; i < n; i++) {
      int k = i+1;
      columns[i] = new ColumnBriefInfo(metaData.getColumnName(k),
                                       metaData.getColumnTypeName(k),
                                       metaData.getColumnType(k));
    }
    setupColumns(columns);
  }

  protected void setupColumns(@NotNull final ColumnBriefInfo[] columns) {
    this.myColumns = columns;
  }

  @Override
  public abstract S collectRows(@NotNull final ResultSet rset) throws SQLException;

}

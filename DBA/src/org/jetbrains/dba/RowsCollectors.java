package org.jetbrains.dba;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class RowsCollectors {
  public static <R> RowsCollector<R> oneRow(@NotNull final Class<R> rowClass) {
    return new OneRowCollector<R>(rowClass);
  }


  public static <R> RowsCollector<List<R>> list(@NotNull final Class<R> rowClass) {
    return new ListCollector<R>(rowClass);
  }


  private static final class OneRowCollector<R> extends RowsCollector<R> {
    @NotNull
    private final Class<R> rowClass;

    @NotNull
    private final RowFetcher<R> rowFetcher;


    private OneRowCollector(@NotNull final Class<R> rowClass) {
      this.rowClass = rowClass;
      this.rowFetcher = RowFetchers.createFor(rowClass);
    }


    @Override
    public boolean expectManyRows() {
      return false;
    }


    @Override
    public R collectRows(@NotNull final ResultSet rset)
      throws SQLException {
      if (rset.next()) {
        rowFetcher.init(rset);
        R row = rowFetcher.fetchRow(rset);
        return row;
      }
      else {
        return null;
      }
    }
  }



  private static final class ListCollector<R> extends RowsCollector<List<R>> {
    @NotNull
    private final Class<R> rowClass;

    @NotNull
    private final RowFetcher<R> rowFetcher;


    private ListCollector(@NotNull final Class<R> rowClass) {
      this.rowClass = rowClass;
      this.rowFetcher = RowFetchers.createFor(rowClass);
    }


    @Override
    public boolean expectManyRows() {
      return true;
    }


    @Override
    public List<R> collectRows(@NotNull final ResultSet rset)
      throws SQLException {
      rowFetcher.init(rset);
      ImmutableList.Builder<R> builder = ImmutableList.builder();
      while (rset.next()) {
        R row = rowFetcher.fetchRow(rset);
        builder.add(row);
      }
      return builder.build();
    }
  }
}

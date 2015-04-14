package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
final class ArrayRowFetcher<C> extends RowFetcher<C[]> {

  @NotNull
  final Class<C> componentClass;

  private final int columnCount;

  private final ValueGetter<C>[] getters;


  ArrayRowFetcher(final ColumnBriefInfo[] columns, @NotNull final Class<C> componentClass) {
    final int n = columns.length;
    this.columnCount = n;
    this.componentClass = componentClass;

    //noinspection unchecked
    this.getters = (ValueGetter<C>[]) instantiateArray(ValueGetter.class, n);
    for (int i = 0; i < n; i++) {
      this.getters[i] = ValueGetters.of(columns[i].jdbcType, componentClass);
    }
  }


  @Override
  C[] fetchRow(@NotNull final ResultSet rset) throws SQLException {
    C[] row = instantiateArray(componentClass, columnCount);
    for (int i = 0; i < columnCount; i++) {
      row[i] = getters[i].getValue(rset, i + 1);
    }
    return row;
  }


  @SuppressWarnings("unchecked")
  private static <X> X[] instantiateArray(final Class<X> componentClass, final int n) {
    return (X[])Array.newInstance(componentClass, n);
  }

}

package org.jetbrains.dba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.core.errors.DBPreparingError;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
final class ArrayRowFetcher<C> extends RowFetcher<C[]> {
  @NotNull
  final Class<C> componentClass;

  @NotNull
  final ValueGetter<C> componentGetter;

  private final Object initSync = new Object();

  // cached count of columns
  private transient int columnCount = 0;


  ArrayRowFetcher(@NotNull final Class<C> componentClass) {
    this.componentClass = componentClass;

    final ValueGetter<C> theComponentGetter = ValueGetters.find(componentClass);
    if (theComponentGetter == null) {
      throw new DBPreparingError("The component of an array should be of a primitive type but given " + componentClass.getSimpleName());
    }

    this.componentGetter = theComponentGetter;
  }


  @Override
  void init(@NotNull final ResultSet rset)
    throws SQLException {
    if (columnCount == 0) {
      synchronized (initSync) {
        if (columnCount == 0) {
          final ResultSetMetaData metaData = rset.getMetaData();
          columnCount = metaData.getColumnCount();
        }
      }
    }
  }


  @Override
  C[] fetchRow(@NotNull final ResultSet rset)
    throws SQLException {
    C[] row = instantiateArray();
    for (int i = 0; i < columnCount; i++) {
      row[i] = componentGetter.getValue(rset, i + 1);
    }
    return row;
  }


  @SuppressWarnings("unchecked")
  private C[] instantiateArray() {
    return (C[])Array.newInstance(componentClass, columnCount);
  }
}

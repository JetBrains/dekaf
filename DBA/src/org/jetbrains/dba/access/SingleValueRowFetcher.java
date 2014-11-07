package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.errors.DBPreparingError;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
final class SingleValueRowFetcher<V> extends RowFetcher<V> {
  @NotNull
  final ValueGetter<V> getter;


  SingleValueRowFetcher(@NotNull final Class<V> valueClass) {
    final ValueGetter<V> theGetter = ValueGetters.find(valueClass);
    if (theGetter == null) {
      throw new DBPreparingError("Unknown how to get value of class " + valueClass.getSimpleName());
    }

    this.getter = theGetter;
  }


  SingleValueRowFetcher(@NotNull final ValueGetter<V> valueGetter) {
    this.getter = valueGetter;
  }


  @Override
  V fetchRow(@NotNull final ResultSet rset)
    throws SQLException {
    return getter.getValue(rset, 1);
  }
}

package org.jetbrains.jdba.core1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.exceptions.DBException;
import org.jetbrains.jdba.exceptions.DBPreparingException;



/**
 * RowFetcher factory.
 *
 * @author Leonid Bushuev from JetBrains
 */
public final class RowFetchers {

  @SuppressWarnings("unchecked")
  static <R> RowFetcher<R> createFor(@NotNull final ColumnBriefInfo[] columns, Class<R> rowClass) {
    // check whether it is a known primitive
    final ValueGetter<?> singleGetter = ValueGetters.find(columns[0].jdbcType, rowClass);
    if (singleGetter != null) {
      return new SingleValueRowFetcher<R>((ValueGetter<R>)singleGetter);
    }

    // check whether it is an array
    if (rowClass.isArray()) {
      final Class<?> componentClass = rowClass.getComponentType();
      assert componentClass != null;
      return new ArrayRowFetcher(columns, componentClass);
    }

    // check whether it is a structure
    try {
      if (rowClass.getDeclaredConstructor() != null && rowClass.getDeclaredFields().length > 0) {
        return new StructRowFetcher<R>(columns, rowClass);
      }
    }
    catch (DBException dbe) {
      throw dbe;
    }
    catch (Exception e) {
      throw new DBPreparingException("Failing to introspect class " + rowClass.getSimpleName(), e);
    }

    // unknown
    throw new DBPreparingException("Unknown how to fetch values of class " + rowClass.getSimpleName());
  }
}

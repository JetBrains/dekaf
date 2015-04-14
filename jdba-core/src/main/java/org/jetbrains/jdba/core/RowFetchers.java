package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.errors.DBError;
import org.jetbrains.jdba.core.errors.DBPreparingError;



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
    catch (DBError dbe) {
      throw dbe;
    }
    catch (Exception e) {
      throw new DBPreparingError("Failing to introspect class " + rowClass.getSimpleName(), e);
    }

    // unknown
    throw new DBPreparingError("Unknown how to fetch values of class " + rowClass.getSimpleName());
  }
}

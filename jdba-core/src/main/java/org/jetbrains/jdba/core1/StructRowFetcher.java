package org.jetbrains.jdba.core1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.exceptions.DBFetchingException;
import org.jetbrains.jdba.core.exceptions.DBPreparingException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;



/**
 * Struct - a class with several public read-write fields.
 *
 * @param <R> class for each row;
 *            must have several read-write fields
 *            with names that are exactly as in the related query,
 *            and one constructor without parameters.
 *            Private, final and transient fields are ignored.
 * @author Leonid Bushuev from JetBrains
 */
final class StructRowFetcher<R> extends RowFetcher<R> {

  @NotNull
  final Class<R> rowClass;

  @NotNull
  final Constructor<R> rowConstructor;

  @NotNull
  final Field[] fields;

  @NotNull
  final ValueGetter<?>[] getters;

  @NotNull
  final transient int[] indices;

  transient boolean initialized;


  StructRowFetcher(final ColumnBriefInfo[] columns, @NotNull final Class<R> rowClass) {
    this.rowClass = rowClass;
    final int N = columns.length;

    try {
      this.rowConstructor = rowClass.getDeclaredConstructor();
      this.rowConstructor.setAccessible(true);

      ArrayList<Field> fields = new ArrayList<Field>(8);
      for (Field field : rowClass.getDeclaredFields()) {
        int modifiers = field.getModifiers();
        if (Modifier.isPrivate(modifiers) || Modifier.isFinal(modifiers) || Modifier.isTransient(modifiers)) {
          continue;
        }
        field.setAccessible(true);
        fields.add(field);
      }

      final int n = fields.size();
      this.fields = fields.toArray(new Field[n]);

      getters = new ValueGetter<?>[n];
      for (int i = 0; i < n; i++) {
        Field f = this.fields[i];
        Class<?> type = f.getType();
        ValueGetter<?> getter;
        if (i < N) {
          getter = ValueGetters.find(columns[i].jdbcType, type);
          if (getter == null) {
            throw new DBPreparingException("Unknown how to getting value for " + rowClass.getSimpleName() + "." + f.getName() + " fo type " + type.getSimpleName());
          }
        }
        else {
          getter = ValueGetters.DumbNullGetter.INSTANCE;
        }
        getters[i] = getter;
      }

      indices = new int[n];
    }
    catch (Exception e) {
      throw new DBPreparingException("Failed to introspect class " + rowClass.getSimpleName() + ": " + e.getMessage(), e);
    }
  }


  @Override
  void init(@NotNull final ResultSet rset)
    throws SQLException {
    if (!initialized) {
      initialize(rset);
    }
  }


  private synchronized void initialize(@NotNull final ResultSet rset)
    throws SQLException {
    if (initialized) {
      return;
    }

    final ResultSetMetaData metaData = rset.getMetaData();
    int m = metaData.getColumnCount();

    for (int i = 0, n = fields.length; i < n; i++) {
      String fieldName = fields[i].getName();
      int index = 0;
      for (int j = 1; j <= m; j++) {
        if (metaData.getColumnName(j).equalsIgnoreCase(fieldName)) {
          index = j;
          break;
        }
      }
      if (index == 0) {
        throw new DBPreparingException("Cursor has no column named \"" + fieldName + "\".");
      }
      indices[i] = index;
    }

    initialized = true;
  }


  @Override
  R fetchRow(@NotNull final ResultSet rset)
    throws SQLException {
    try {
      R struct = rowConstructor.newInstance();
      for (int i = 0, n = indices.length; i < n; i++) {
        int index = indices[i];
        ValueGetter<?> getter = getters[i];
        Field field = fields[i];

        Object value = getter.getValue(rset, index);

        if (value != null) {
          field.set(struct, value);
        }
      }
      return struct;
    }
    catch (SQLException sqle) {
      throw sqle;
    }
    catch (Exception e) {
      throw new DBFetchingException("Failed to instantiate and/or populate class " + rowClass.getSimpleName(), null);
    }
  }
}

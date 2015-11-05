package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.exceptions.UnexpectedDBException;
import org.jetbrains.dekaf.exceptions.UnexpectedReflectionException;
import org.jetbrains.dekaf.util.NameAndClass;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcRowFetchers {




  public static <V> OneValueFetcher<V> createOneValueFetcher(final int position,
                                                             final JdbcValueGetter<V> getter) {
    return new OneValueFetcher<V>(position,getter);
  }

  public static <V> ArrayFetcher<V> createArrayFetcher(final int position,
                                                       final Class<V> commonClass,
                                                       final JdbcValueGetter<? extends V>[] getters) {
    return new ArrayFetcher<V>(position, commonClass, getters);
  }

  public static IntArrayFetcher createIntArrayFetcher(final int position) {
    return new IntArrayFetcher(position);
  }

  public static LongArrayFetcher createLongArrayFetcher(final int position) {
    return new LongArrayFetcher(position);
  }

  public static TupleFetcher createTupleFetcher(final NameAndClass[] components) {
    return new TupleFetcher(components);
  }

  public static <S> StructFetcher<S> createStructFetcher(final Class<S> structClass,
                                                         final NameAndClass[] components) {
    return new StructFetcher<S>(structClass, components);
  }


  //// FETCHERS \\\\

  public static final class OneValueFetcher<V> extends JdbcRowFetcher<V> {

    private final int position;
    private final JdbcValueGetter<V> getter;


    OneValueFetcher(final int position, final JdbcValueGetter<V> getter) {
      this.position = position;
      this.getter = getter;
    }

    @Override
    V fetchRow(@NotNull final ResultSet rset) throws SQLException {
      return getter.getValue(rset, position);
    }
  }


  public static final class ArrayFetcher<V> extends JdbcRowFetcher<V[]> {

    private final int position;
    private final Class<V> commonClass;
    private final JdbcValueGetter<? extends V>[] getters;


    private ArrayFetcher(final int position,
                         final Class<V> commonClass,
                         final JdbcValueGetter<? extends V>[] getters) {
      this.position = position;
      this.commonClass = commonClass;
      this.getters = getters;
    }


    @Override
    V[] fetchRow(@NotNull final ResultSet rset) throws SQLException {
      final int n = getters.length;
      //noinspection unchecked
      V[] array = (V[]) Array.newInstance(commonClass, n);
      for (int j = 0; j < n; j++) {
        array[j] = getters[j].getValue(rset, position + j);
      }
      return array;
    }
  }


  public static final class IntArrayFetcher extends JdbcRowFetcher<int[]> {

    private final int position;

    private JdbcValueGetter<Integer>[] getters;

    private IntArrayFetcher(final int position) {
      this.position = position;
    }

    private void init(@NotNull final ResultSetMetaData md) throws SQLException {
      int n = Math.max(md.getColumnCount() - (position-1), 0);
      //noinspection unchecked
      getters = (JdbcValueGetter<Integer>[]) new JdbcValueGetter[n];
      for (int i = 0; i < n; i++) {
        int jdbcType = md.getColumnType(position + i);
        JdbcValueGetter<Integer> valueGetter = JdbcValueGetters.of(jdbcType, int.class);
        getters[i] = valueGetter;
      }
    }

    @Override
    int[] fetchRow(@NotNull final ResultSet rset) throws SQLException {
      if (getters == null) init(rset.getMetaData());

      final int n = getters.length;
      //noinspection unchecked
      int[] array = new int[n];
      for (int j = 0; j < n; j++) {
        Integer value = getters[j].getValue(rset, position + j);
        array[j] = value == null ? 0 : value;
      }
      return array;
    }
  }


  public static final class LongArrayFetcher extends JdbcRowFetcher<long[]> {

    private final int position;

    private JdbcValueGetter<Long>[] getters;

    private LongArrayFetcher(final int position) {
      this.position = position;
    }

    private void init(@NotNull final ResultSetMetaData md) throws SQLException {
      int n = Math.max(md.getColumnCount() - (position-1), 0);
      //noinspection unchecked
      getters = (JdbcValueGetter<Long>[]) new JdbcValueGetter[n];
      for (int i = 0; i < n; i++) {
        int jdbcType = md.getColumnType(position + i);
        JdbcValueGetter<Long> valueGetter = JdbcValueGetters.of(jdbcType, long.class);
        getters[i] = valueGetter;
      }
    }

    @Override
    long[] fetchRow(@NotNull final ResultSet rset) throws SQLException {
      if (getters == null) init(rset.getMetaData());

      final int n = getters.length;
      //noinspection unchecked
      long[] array = new long[n];
      for (int j = 0; j < n; j++) {
        Long value = getters[j].getValue(rset, position + j);
        array[j] = value == null ? 0L : value;
      }
      return array;
    }
  }


  public static abstract class ComplexFetcher<X> extends JdbcRowFetcher<X> {

    protected final NameAndClass[] components;
    protected final int[] columnIndices;
    protected final JdbcValueGetter<?>[] getters;

    protected boolean myRequiresInit = true;


    public ComplexFetcher(@NotNull final NameAndClass[] components) {
      this.components = components;

      int n = components.length;
      columnIndices = new int[n];
      getters = new JdbcValueGetter[n];
    }


    protected void initGetters(@NotNull final ResultSetMetaData md) {
      try {
        int n = components.length;
        Map<String, Integer> columnNames =
            new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        for (int j = 1, cn = md.getColumnCount(); j <= cn; j++) {
          String columnName = md.getColumnName(j);
          columnNames.put(columnName, j);
        }

        for (int i = 0; i < n; i++) {
          String name = components[i].name;
          Integer columnIndex = columnNames.get(name);
          if (columnIndex == null) continue;
          int jdbcType = md.getColumnType(columnIndex);
          JdbcValueGetter valueGetter = JdbcValueGetters.of(jdbcType, components[i].clazz);
          columnIndices[i] = columnIndex;
          getters[i] = valueGetter;
        }
      }
      catch (SQLException sqle) {
        throw new UnexpectedDBException("Analysing metadata of the query result", sqle, null);
      }

      myRequiresInit = false;
    }
  }


  public static final class TupleFetcher extends ComplexFetcher<Object[]> {


    public TupleFetcher(@NotNull final NameAndClass[] components) {
      super(components);
    }


    @Override
    Object[] fetchRow(@NotNull final ResultSet rset) throws SQLException {
      if (myRequiresInit) initGetters(rset.getMetaData());

      Object[] tuple = new Object[components.length];

      for (int i = 0, n = columnIndices.length; i < n; i++) {
        int columnIndex = columnIndices[i];
        JdbcValueGetter<?> g = getters[i];
        if (columnIndex <= 0 || g == null) continue;
        Object value = g.getValue(rset, columnIndex);
        tuple[i] = value;
      }

      return tuple;
    }

  }


  public static final class StructFetcher<S> extends ComplexFetcher<S> {

    private final Class<S> structClass;
    private final Constructor<S> structConstructor;
    private final Field[] fields;

    private boolean myRequiresInit = true;


    public StructFetcher(@NotNull final Class<S> structClass,
                         @NotNull final NameAndClass[] components) {
      super(components);

      this.structClass = structClass;
      int n = components.length;
      fields = new Field[n];

      try {
        this.structConstructor = structClass.getDeclaredConstructor();
        this.structConstructor.setAccessible(true);

        for (int i = 0; i < n; i++) {
          String name = components[i].name;
          Field f = getClassField(structClass, name);
          if (f != null) {
            f.setAccessible(true);
            fields[i] = f;
          }
        }
      }
      catch (Exception e) {
        throw new UnexpectedReflectionException("Failed to analyze class " + structClass.getName(), e);
      }
    }



    @Override
    S fetchRow(@NotNull final ResultSet rset) throws SQLException {
      if (myRequiresInit) initGetters(rset.getMetaData());

      try {
        final S struct = structConstructor.newInstance();
        for (int i = 0, n = columnIndices.length; i < n; i++) {
          int columnIndex = columnIndices[i];
          Field f = fields[i];
          JdbcValueGetter<?> g = getters[i];
          if (columnIndex <= 0 || f == null || g == null) continue;
          Object value = g.getValue(rset, columnIndex);
          if (value != null) {
            f.set(struct, value);
          }
        }
        return struct;
      }
      catch (InstantiationException e) {
        throw new UnexpectedReflectionException("Failed to create/populate class " + structClass, e);
      }
      catch (IllegalAccessException e) {
        throw new UnexpectedReflectionException("Failed to create/populate class " + structClass, e);
      }
      catch (InvocationTargetException e) {
        throw new UnexpectedReflectionException("Failed to create/populate class " + structClass, e);
      }
    }

  }


  //// OTHER FUNCTIONS \\\\

  protected static Field getClassField(final @NotNull Class<?> structClass, final String name)
      throws NoSuchFieldException
  {
    return structClass.getDeclaredField(name);
  }


}

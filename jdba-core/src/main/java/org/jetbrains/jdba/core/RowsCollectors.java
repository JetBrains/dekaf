package org.jetbrains.jdba.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.exceptions.DBFetchingException;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class RowsCollectors {

  /**
   * A collector that gets the first row from the result set.
   *
   * <p>
   *   This collector returns the first row, or null if query returned no rows.
   * </p>
   *
   * @param rowClass   class of the row.
   * @param <R>        class of the row.
   * @return           created collector.
   */
  public static <R> DBRowsCollector<R> oneRow(@NotNull final Class<R> rowClass) {
    return new OneRowCollector<R>(rowClass);
  }


  /**
   * A collector that collects rows as a list, preserving order of rows.
   *
   * <p>
   *   This collector returns an immutable list of rows,
   *   or an empty list if it was no rows in the result set.
   * </p>
   *
   * @param rowClass   class of each row.
   * @param <R>        class of each row.
   * @return           created collector.
   */
  public static <R> DBRowsCollector<List<R>> list(@NotNull final Class<R> rowClass) {
    return new ListCollector<R>(rowClass);
  }


  /**
   * A collector that collects rows as an array, preserving order of rows.
   *
   * <p>
   *   This collector returns an array of rows,
   *   or an empty array if it was no rows in the result set.
   * </p>
   *
   * @param rowClass   class of each row.
   * @param <R>        class of each row.
   * @return           created collector.
   */
  public static <R> DBRowsCollector<R[]> array(@NotNull final Class<R> rowClass) {
    return new ArrayCollector<R>(rowClass);
  }


  /**
   * A collector that collects a map.
   *
   * <p>
   *   This collector collects an immutable map of key -> value.
   *   All keys and all values must be non-null.
   *   All keys must be different.
   * </p>
   *
   * @param keyClass     class of the key.
   * @param valueClass   class of the value.
   * @param <K>          class of the key.
   * @param <V>          class of the value.
   * @return             created map collector.
   */
  public static <K,V> DBRowsCollector<Map<K,V>> map(@NotNull final Class<K> keyClass,
                                                    @NotNull final Class<V> valueClass) {
    return new MapCollector<K,V>(keyClass, valueClass);
  }


  /**
   * A collector that just checks whether the result set has at least one row.
   *
   * @return the collector.
   */
  public static DBRowsCollector<Boolean> rowsExist() {
    return ourRowsExistCollector;
  }



  private static final class OneRowCollector<R> extends BaseRowsCollector<R> {
    @NotNull
    private final Class<R> rowClass;

    private RowFetcher<R> rowFetcher = null;


    private OneRowCollector(@NotNull final Class<R> rowClass) {
      this.rowClass = rowClass;
    }

    @Override
    protected void setupColumns(@NotNull final ColumnBriefInfo[] columns) {
      super.setupColumns(columns);

      assert columns.length > 0 : "Empty MetaData";
      this.rowFetcher = RowFetchers.createFor(columns, rowClass);
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
        //noinspection UnnecessaryLocalVariable
        R row = rowFetcher.fetchRow(rset);
        return row;
      }
      else {
        return null;
      }
    }
  }



  private static final class ListCollector<R> extends BaseRowsCollector<List<R>> {
    @NotNull
    private final Class<R> rowClass;

    @Nullable
    private RowFetcher<R> rowFetcher;

    private ListCollector(@NotNull final Class<R> rowClass) {
      this.rowClass = rowClass;
    }

    @Override
    protected void setupColumns(@NotNull final ColumnBriefInfo[] columns) {
      super.setupColumns(columns);
      this.rowFetcher = RowFetchers.createFor(columns, rowClass);
    }

    @Override
    public boolean expectManyRows() {
      return true;
    }


    @Override
    public List<R> collectRows(@NotNull final ResultSet rset) throws SQLException {
      assert rowFetcher != null;
      rowFetcher.init(rset);
      ImmutableList.Builder<R> builder = ImmutableList.builder();
      while (rset.next()) {
        R row = rowFetcher.fetchRow(rset);
        builder.add(row);
      }
      return builder.build();
    }
  }


  public static final class ArrayCollector<R> extends BaseRowsCollector<R[]> {

    @NotNull
    private final ListCollector<R> myListCollector;

    @NotNull
    private final Class<R> myRowClass;


    public ArrayCollector(@NotNull final Class<R> rowClass) {
      myListCollector = new ListCollector<R>(rowClass);
      myRowClass = rowClass;
    }

    @Override
    protected void setupColumns(@NotNull final ColumnBriefInfo[] columns) {
      super.setupColumns(columns);
      myListCollector.setupColumns(columns);
    }

    @Override
    public R[] collectRows(@NotNull ResultSet rset) throws SQLException {
      List<R> list = myListCollector.collectRows(rset);
      int n = list.size();

      @SuppressWarnings("unchecked")
      R[] array = (R[])Array.newInstance(myRowClass, n);

      return list.toArray(array);
    }


    @Override
    public boolean expectManyRows() {
      return true;
    }
  }


  private static final class MapCollector<K,V> extends BaseRowsCollector<Map<K,V>> {
    @NotNull
    private final Class<K> myKeyClass;

    @NotNull
    private final Class<V> myValueClass;

    private ValueGetter<K> myKeyGetter = null;

    private ValueGetter<V> myValueGetter = null;


    public MapCollector(@NotNull final Class<K> keyClass,
                        @NotNull final Class<V> valueClass) {
      myKeyClass = keyClass;
      myValueClass = valueClass;
    }

    @Override
    protected void setupColumns(@NotNull final ColumnBriefInfo[] columns) {
      super.setupColumns(columns);

      if (columns.length < 2) {
        String message = String.format("The cursor contain too few columns to fetch a map (expected 2 but got %d)",
                                       columns.length);
        throw new DBFetchingException(message, String.format("Fetching %s -> %S", myKeyClass.getSimpleName(), myValueClass.getSimpleName()));
      }

      myKeyGetter = ValueGetters.of(columns[0].jdbcType, myKeyClass);
      myValueGetter = ValueGetters.of(columns[1].jdbcType, myValueClass);
    }

    @Override
    public boolean expectManyRows() {
      return true;
    }


    @Override
    public Map<K,V> collectRows(@NotNull final ResultSet rset)
      throws SQLException
    {
      ImmutableMap.Builder<K,V> builder = ImmutableMap.builder();
      while (rset.next()) {
        K k = myKeyGetter.getValue(rset, 1);
        V v = myValueGetter.getValue(rset, 2);
        if (k == null) throw new DBFetchingException("Got null key when collecting rows for a map of {"+myKeyClass.getSimpleName()+"->"+myValueClass.getSimpleName()+"}.", null);
        if (v == null) throw new DBFetchingException("Got null value when collecting rows for a map of {"+myKeyClass.getSimpleName()+"->"+myValueClass.getSimpleName()+"}.", null);
        builder.put(k, v);
      }
      return builder.build();
    }
  }


  private static final class RowsExistenceCollector extends BaseRowsCollector<Boolean> {

    @Override
    public Boolean collectRows(@NotNull final ResultSet rset) throws SQLException {
      return rset.next();
    }


    @Override
    public boolean expectManyRows() {
      return false;
    }
  }

  private static final RowsExistenceCollector ourRowsExistCollector = new RowsExistenceCollector();


}

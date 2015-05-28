package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcRowsCollectors {


  public static ExistenceCollector createExistenceCollector() {
    return new ExistenceCollector();
  }

  public static <R> SingleRowCollector<R> createSingleRowCollector(final JdbcRowFetcher<R> fetcher) {
    return new SingleRowCollector<R>(fetcher);
  }

  public static <R> ArrayCollector<R> createArrayCollector(final JdbcRowFetcher<R> fetcher) {
    return new ArrayCollector<R>(fetcher);
  }

  public static ArrayOfIntsCollector createArrayOfIntsCollector(int initialCapacity) {
    return new ArrayOfIntsCollector(initialCapacity);
  }

  public static ArrayOfLongsCollector createArrayOfLongsCollector(int initialCapacity) {
    return new ArrayOfLongsCollector(initialCapacity);
  }

  public static <R> ListCollector<R> createListCollector(final JdbcRowFetcher<R> fetcher) {
    return new ListCollector<R>(fetcher);
  }

  public static <K,V> HashMapCollector<K,V> createHashMapCollector(final JdbcValueGetter<K> keyGetter,
                                                                   final JdbcValueGetter<V> valueGetter) {
    return new HashMapCollector<K, V>(keyGetter, valueGetter);
  }

  public static <K extends Comparable<K>, V> SortedMapCollector<K,V> createSortedMapCollector(
                                                                final JdbcValueGetter<K> keyGetter,
                                                                final JdbcValueGetter<V> valueGetter) {
    return new SortedMapCollector<K, V>(keyGetter, valueGetter);
  }


  //// COLLECTORS \\\\

  protected static class ExistenceCollector extends JdbcRowsCollector<Boolean> {

    @Override
    protected Boolean collectRows(@NotNull final ResultSet rset, final int limit) {
      return hasMoreRows;
    }
  }

  protected static class SingleRowCollector<R> extends JdbcRowsCollector<R> {

    private final JdbcRowFetcher<R> fetcher;

    private SingleRowCollector(final JdbcRowFetcher<R> fetcher) {
      this.fetcher = fetcher;
    }

    @Override
    protected R collectRows(@NotNull final ResultSet rset, final int limit) throws SQLException {
      R result = fetcher.fetchRow(rset);
      hasMoreRows = rset.next();
      return result;
    }
  }


  protected static class ArrayCollector<R> extends JdbcRowsCollector<R[]> {

    private final JdbcRowFetcher<R> fetcher;

    private ArrayCollector(final JdbcRowFetcher<R> fetcher) {
      this.fetcher = fetcher;
    }

    @Override
    protected R[] collectRows(@NotNull final ResultSet rset, final int limit) throws SQLException {
      List<R> list = new ArrayList<R>(Math.min(limit, 1000));

      while (hasMoreRows && list.size() < limit) {
        R row = fetcher.fetchRow(rset);
        list.add(row);
        hasMoreRows = rset.next();
      }

      int n = list.size();
      //noinspection unchecked
      R[] array;
      if (n > 0) {
        Class<?> rowClass = list.get(0).getClass();
        //noinspection unchecked
        array = (R[]) Array.newInstance(rowClass, n);
        array = list.toArray(array);
      }
      else {
        //noinspection unchecked
        array = (R[]) new Object[0];
      }

      return array;
    }
  }


  protected static class ArrayOfIntsCollector extends JdbcRowsCollector<int[]> {

    private final int initialCapacity;

    private ArrayOfIntsCollector(final int initialCapacity) {
      this.initialCapacity = initialCapacity;
    }

    @Override
    protected int[] collectRows(@NotNull final ResultSet rset, final int limit)
            throws SQLException
    {
      int k = 0;
      int[] array = new int[initialCapacity];
      int len = array.length;

      while (hasMoreRows && k < limit) {

        if (k >= len) {
          int newSize = len <= 4096
                                ? len << 1
                                : len + (len >> 1);
          array = Arrays.copyOf(array, newSize);
          len = array.length;
        }

        int value = rset.getInt(1);
        array[k++] = value;
        hasMoreRows = rset.next();
      }

      if (k != array.length) {
        array = Arrays.copyOf(array, k);
      }

      return array;
    }
  }


  protected static class ArrayOfLongsCollector extends JdbcRowsCollector<long[]> {

    private final int initialCapacity;

    private ArrayOfLongsCollector(final int initialCapacity) {
      this.initialCapacity = initialCapacity;
    }

    @Override
    protected long[] collectRows(@NotNull final ResultSet rset, final int limit)
            throws SQLException
    {
      int k = 0;
      long[] array = new long[initialCapacity];
      int len = array.length;

      while (hasMoreRows && k < limit) {

        if (k >= len) {
          int newSize = len <= 4096
                                ? len << 1
                                : len + (len >> 1);
          array = Arrays.copyOf(array, newSize);
          len = array.length;
        }

        long value = rset.getLong(1);
        array[k++] = value;
        hasMoreRows = rset.next();
      }

      if (k != array.length) {
        array = Arrays.copyOf(array, k);
      }

      return array;
    }
  }


  protected static class ListCollector<R> extends JdbcRowsCollector<List<R>> {

    private final JdbcRowFetcher<R> fetcher;

    private ListCollector(final JdbcRowFetcher<R> fetcher) {
      this.fetcher = fetcher;
    }

    @Override
    protected List<R> collectRows(@NotNull final ResultSet rset, final int limit) throws SQLException {
      List<R> result = new ArrayList<R>(Math.min(limit, 1000));

      while (hasMoreRows && result.size() < limit) {
        R row = fetcher.fetchRow(rset);
        result.add(row);
        hasMoreRows = rset.next();
      }

      return result;
    }
  }


  protected static class HashMapCollector<K,V> extends JdbcRowsCollector<Map<K,V>> {

    private final JdbcValueGetter<K> keyGetter;
    private final JdbcValueGetter<V> valueGetter;

    private HashMapCollector(final JdbcValueGetter<K> keyGetter,
                             final JdbcValueGetter<V> valueGetter) {
      this.keyGetter = keyGetter;
      this.valueGetter = valueGetter;
    }

    @Override
    protected Map<K,V> collectRows(@NotNull final ResultSet rset, final int limit) throws SQLException {
      Map<K,V> result = new HashMap<K,V>(Math.min(limit, 1000));

      while (hasMoreRows && result.size() < limit) {
        K k = keyGetter.getValue(rset, 1);
        V v = valueGetter.getValue(rset, 2);
        result.put(k, v);
        hasMoreRows = rset.next();
      }

      return result;
    }
  }


  protected static class SortedMapCollector<K extends Comparable<K>, V> extends JdbcRowsCollector<Map<K,V>> {

    private final JdbcValueGetter<K> keyGetter;
    private final JdbcValueGetter<V> valueGetter;

    private SortedMapCollector(final JdbcValueGetter<K> keyGetter,
                              final JdbcValueGetter<V> valueGetter) {
      this.keyGetter = keyGetter;
      this.valueGetter = valueGetter;
    }

    @Override
    protected Map<K,V> collectRows(@NotNull final ResultSet rset, final int limit) throws SQLException {
      Map<K,V> result = new TreeMap<K,V>();

      while (hasMoreRows && result.size() < limit) {
        K k = keyGetter.getValue(rset, 1);
        V v = valueGetter.getValue(rset, 2);
        result.put(k, v);
        hasMoreRows = rset.next();
      }

      return result;
    }
  }

}

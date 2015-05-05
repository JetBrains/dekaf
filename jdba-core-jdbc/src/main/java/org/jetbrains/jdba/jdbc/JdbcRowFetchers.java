package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;



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


}

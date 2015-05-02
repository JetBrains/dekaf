package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcRowsCollectors {


  public static <R> SingleRowCollector<R> createSingleRowCollector(final JdbcRowFetcher<R> fetcher) {
    return new SingleRowCollector<R>(fetcher);
  }

  public static <R> ArrayCollector<R> createArrayCollector(final JdbcRowFetcher<R> fetcher) {
    return new ArrayCollector<R>(fetcher);
  }

  public static <R> ListCollector<R> createListCollector(final JdbcRowFetcher<R> fetcher) {
    return new ListCollector<R>(fetcher);
  }



  //// COLLECTORS \\\\

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

}

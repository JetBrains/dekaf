package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.ResultLayout;
import org.jetbrains.dekaf.core.RowLayout;
import org.jetbrains.dekaf.exceptions.DBPreparingException;
import org.jetbrains.dekaf.intermediate.DBIntermediateConsts;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateCursor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static java.lang.Math.min;
import static java.lang.String.format;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcIntermediateCursor<R> implements IntegralIntermediateCursor<R> {

  @NotNull
  private final JdbcIntermediateSeance mySeance;

  @NotNull
  private final ResultSet myResultSet;

  @NotNull
  protected final ResultLayout<R> myResultLayout;

  private final boolean myIsDefault;

  private boolean myOpened;

  private boolean myHasRows;

  private int myFetchLimit = DBIntermediateConsts.DEFAULT_FETCH_LIMIT;

  private JdbcRowsCollector<R> myRowsCollector;



  protected JdbcIntermediateCursor(@NotNull final JdbcIntermediateSeance seance,
                                   @NotNull final ResultSet resultSet,
                                   @NotNull final ResultLayout<R> resultLayout,
                                   final boolean isDefault,
                                   @Nullable final Boolean hasRows) {
    mySeance = seance;
    myResultSet = resultSet;
    myResultLayout = resultLayout;
    myIsDefault = isDefault;

    try {
      myOpened = !JdbcUtil.isClosed(myResultSet);
    }
    catch (SQLException sqle) {
      throw seance.mySession.recognizeException(sqle, seance.myStatementText);
    }

    if (myOpened) {
      if (hasRows == null) {
        try {
          myHasRows = resultSet.next(); // attempt to get the first portion of rows
        }
        catch (SQLException sqle) {
          throw seance.mySession.recognizeException(sqle, seance.myStatementText);
        }
      }
      else {
        myHasRows = hasRows;
      }
    }
    else {
      myHasRows = false;
    }

    if (myHasRows) {
      try {
        myRowsCollector = createRowsCollector(resultLayout, myResultSet, seance.myStatementText);
      }
      catch (SQLException sqle) {
        throw seance.mySession.recognizeException(sqle, seance.myStatementText);
      }
    }
  }


  private static <R> JdbcRowsCollector<R> createRowsCollector(final ResultLayout<R> resultLayout,
                                                              final ResultSet resultSet,
                                                              final String statementText) throws SQLException{
    RowLayout<?> rowLayout = resultLayout.row;

    int n;
    switch (rowLayout.kind) {
      case EXISTENCE:
        n = 0;
        break;
      case ONE_VALUE:
        n = 1;
        break;
      default:
        n = rowLayout.components.length;
    }

    final ResultSetMetaData metaData = resultSet.getMetaData();
    final int m = metaData.getColumnCount();

    final JdbcValueGetter<?>[] getters;
    if (n > 0 && rowLayout.kind != RowLayout.Kind.TUPLE && rowLayout.kind != RowLayout.Kind.STRUCT) {
      // array with specified components
      getters = new JdbcValueGetter[n];

      if (n > m && rowLayout.kind == RowLayout.Kind.ARRAY) {
        throw new DBPreparingException(format("Query returns too few columns: %d when expected %d (row type is %s).",
                                              m, n, resultLayout.row.rowClass),
                                       statementText);
      }

      for (int i = 0; i < min(m,n); i++) {
        int jdbcType = metaData.getColumnType(i + 1);
        getters[i] = JdbcValueGetters.of(jdbcType, rowLayout.components[i].clazz);
      }
    }
    else if (n == 0 && rowLayout.commonComponentClass == Object.class) {
      // raw array
      getters = new JdbcValueGetter[m];
      for (int i = 0; i < m; i++) {
        int jdbcType = metaData.getColumnType(i + 1);
        getters[i] = JdbcValueGetters.of(jdbcType, Object.class);
      }
    }
    else {
      // TODO log somehow?
      getters = null;
    }

    final JdbcRowFetcher<?> fetcher;
    switch (rowLayout.kind) {
      case EXISTENCE:
        fetcher = NOTHING_FETCHER;
        break;
      case ONE_VALUE:
        assert getters != null && getters.length > 0;
        fetcher = JdbcRowFetchers.createOneValueFetcher(1, getters[0]);
        break;
      case ARRAY:
        if (rowLayout.commonComponentClass == int.class) {
          fetcher = JdbcRowFetchers.createIntArrayFetcher(1);
        }
        else if (rowLayout.commonComponentClass == long.class) {
          fetcher = JdbcRowFetchers.createLongArrayFetcher(1);
        }
        else {
          fetcher = JdbcRowFetchers.createArrayFetcher(1, rowLayout.commonComponentClass, getters);
        }
        break;
      case TUPLE:
        fetcher = JdbcRowFetchers.createTupleFetcher(rowLayout.components);
        break;
      case STRUCT:
        fetcher = JdbcRowFetchers.createStructFetcher(rowLayout.rowClass, rowLayout.components);
        break;
      default:
        throw new DBPreparingException(format("Unknown how to handle the row layout %s", rowLayout.kind.toString()),
                                       statementText);
    }

    final JdbcRowsCollector<?> collector;
    switch (resultLayout.kind) {
      case EXISTENCE:
        collector = JdbcRowsCollectors.createExistenceCollector();
        break;
      case SINGLE_ROW:
        collector = JdbcRowsCollectors.createSingleRowCollector(fetcher);
        break;
      case ARRAY:
        collector = JdbcRowsCollectors.createArrayCollector(fetcher);
        break;
      case ARRAY_OF_PRIMITIVES:
        Class<?> componentClass = resultLayout.row.commonComponentClass;
        if (componentClass == int.class) {
          collector = JdbcRowsCollectors.createArrayOfIntsCollector(resultLayout.initialCapacity);
        }
        else if (componentClass == long.class) {
          collector = JdbcRowsCollectors.createArrayOfLongsCollector(resultLayout.initialCapacity);
        }
        else {
          throw new DBPreparingException("Primitive array of "+componentClass.getName()+" is not supported",
                                         statementText);
        }
        break;
      case LIST:
        collector = JdbcRowsCollectors.createListCollector(fetcher);
        break;
      case MAP:
        if (resultLayout.sorted) {
          collector = JdbcRowsCollectors.createHashMapCollector(getters[0], getters[1]);
        }
        else {
          JdbcValueGetter keyGetter = getters[0];
          collector = JdbcRowsCollectors.createSortedMapCollector(keyGetter, getters[1]);
        }
        break;
      default:
        throw new DBPreparingException(format("Unknown how to handle the result layout %s", resultLayout.kind.toString()),
                                       statementText);
    }

    //noinspection unchecked
    return (JdbcRowsCollector<R>) collector;
  }


  @Override
  public boolean hasRows() {
    return myHasRows;
  }


  @NotNull
  @Override
  public String[] getColumnNames() {
    if (!myOpened) throw new IllegalStateException("The cursor is not opened or is yet closed.");

    try {
      final ResultSetMetaData md = myResultSet.getMetaData();
      int n = md.getColumnCount();
      String[] names = new String[n];
      for (int j = 0; j < n; j++) names[j] = JdbcUtil.getColumnName(md, j+1);
      return names;
    }
    catch (SQLException sqle) {
      throw mySeance.mySession.recognizeException(sqle, mySeance.myStatementText);
    }
  }

  @Override
  public synchronized void setFetchLimit(final int limit) {
    myFetchLimit = limit;
  }

  @Override
  public R fetch() {
    if (!myHasRows) {
      if (myResultLayout.kind == ResultLayout.Kind.EXISTENCE) {
        //noinspection unchecked
        return (R) Boolean.FALSE;
      }
      else {
        return null; // no more rows
      }
    }

    if (!myOpened) throw new IllegalStateException("The cursor is not opened or is yet closed.");

    final R result;
    try {
      result = myRowsCollector.collectRows(myResultSet, myFetchLimit);
    }
    catch (SQLException sqle) {
      close();
      throw mySeance.mySession.recognizeException(sqle, mySeance.myStatementText);
    }

    myHasRows = myRowsCollector.hasMoreRows;
    if (!myHasRows) close();

    return result;
  }

  boolean isOpened() {
    return myOpened;
  }

  @Override
  public synchronized void close() {
    try {
      JdbcUtil.close(myResultSet);
    }
    finally {
      myHasRows = false;
      myOpened = false;
    }
  }


  //// OTHER STUFF \\\\

  private static final JdbcRowFetcher<Void> NOTHING_FETCHER = new JdbcRowFetcher<Void>() {
    @Override
    Void fetchRow(@NotNull final ResultSet rset) { return null; }
  };

}

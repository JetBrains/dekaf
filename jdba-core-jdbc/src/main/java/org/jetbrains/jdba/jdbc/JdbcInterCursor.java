package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.DBInterCursor;
import org.jetbrains.jdba.core.ResultLayout;
import org.jetbrains.jdba.core.RowLayout;
import org.jetbrains.jdba.core.exceptions.DBPreparingException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static java.lang.String.format;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcInterCursor<R> implements DBInterCursor<R> {

  @NotNull
  private final JdbcInterSeance mySeance;

  @NotNull
  private final ResultSet myResultSet;

  @NotNull
  protected final ResultLayout<R> myResultLayout;

  private final boolean myIsDefault;

  private boolean myOpened;

  private boolean myHasRows;

  private int myFetchLimit = 1000000;

  private JdbcRowsCollector<R> myRowsCollector;



  protected JdbcInterCursor(@NotNull final JdbcInterSeance seance,
                            @NotNull final ResultSet resultSet,
                            @NotNull final ResultLayout<R> resultLayout,
                            final boolean isDefault,
                            @Nullable final Boolean hasRows) {
    mySeance = seance;
    myResultSet = resultSet;
    myResultLayout = resultLayout;
    myIsDefault = isDefault;

    try {
      myOpened = !myResultSet.isClosed();
    }
    catch (SQLException sqle) {
      throw seance.session.recognizeException(sqle, seance.statementText);
    }

    if (myOpened) {
      if (hasRows == null) {
        try {
          myHasRows = resultSet.next(); // attempt to get the first portion of rows
        }
        catch (SQLException sqle) {
          throw seance.session.recognizeException(sqle, seance.statementText);
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
        myRowsCollector = createRowsCollector(resultLayout, myResultSet, seance.statementText);
      }
      catch (SQLException sqle) {
        throw seance.session.recognizeException(sqle, seance.statementText);
      }
    }
  }


  private static <R> JdbcRowsCollector<R> createRowsCollector(final ResultLayout<R> resultLayout,
                                                              final ResultSet resultSet,
                                                              final String statementText) throws SQLException{
    RowLayout<?> rowLayout = resultLayout.row;

    ResultSetMetaData metaData = resultSet.getMetaData();

    int n;
    switch (rowLayout.kind) {
      case ONE_VALUE:
        n = 1;
        break;
      default:
        n = rowLayout.componentClasses.length;
    }

    if (n < metaData.getColumnCount()) {
      throw new DBPreparingException(format("Query returns too few columns: %d when expected %d.",
                                            metaData.getColumnCount(),
                                            n));
    }

    JdbcValueGetter<?>[] getters = new JdbcValueGetter[n];
    for (int i = 0; i < n; i++) {
      int jdbcType = metaData.getColumnType(i+1);
      getters[i] = JdbcValueGetters.of(jdbcType, rowLayout.componentClasses[i]);
    }

    JdbcRowFetcher<?> fetcher;
    switch (rowLayout.kind) {
      case ONE_VALUE:
        fetcher = JdbcRowFetchers.createOneValueFetcher(1, getters[0]);
        break;
      case ARRAY:
        fetcher = JdbcRowFetchers.createArrayFetcher(1, rowLayout.commonComponentClass, getters);
        break;
      default:
        throw new DBPreparingException(format("Unknown jow to handle the row layout %s", rowLayout.kind.toString()));
    }

    JdbcRowsCollector<?> collector;
    switch (resultLayout.kind) {
      case SINGLE_ROW:
        collector = JdbcRowsCollectors.createSingleRowCollector(fetcher);
        break;
      case ARRAY:
        collector = JdbcRowsCollectors.createArrayCollector(fetcher);
        break;
      case LIST:
        collector = JdbcRowsCollectors.createListCollector(fetcher);
        break;
      default:
        throw new DBPreparingException(format("Unknown how to handle the result layout %s", resultLayout.kind.toString()));
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

    // TODO implement JdbcInterCursor.getColumnNames()
    throw new RuntimeException("Method JdbcInterCursor.getColumnNames() is not implemented yet.");

  }

  @Override
  public synchronized void setFetchLimit(final int limit) {
    myFetchLimit = limit;
  }

  @Override
  public R fetch() {
    if (!myOpened) throw new IllegalStateException("The cursor is not opened or is yet closed.");

    final R result;
    try {
      result = myRowsCollector.collectRows(myResultSet, myFetchLimit);
    }
    catch (SQLException sqle) {
      close();
      throw mySeance.session.recognizeException(sqle, mySeance.statementText);
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
      if (!myResultSet.isClosed()) {
        myResultSet.close();
      }
    }
    catch (SQLException e) {
      // TODO log somehow
    }
    finally {
      myHasRows = false;
      myOpened = false;
    }
  }
}

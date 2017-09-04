package org.jetbrains.dekaf.jdbc;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.ResultLayout;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateSeance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.jetbrains.dekaf.util.Objects.castTo;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcIntermediateSeance implements IntegralIntermediateSeance {


  //// STATE \\\\

  @NotNull
  protected final JdbcIntermediateSession mySession;

  @NotNull
  protected final String myStatementText;

  protected PreparedStatement myStatement;

  /**
   * In pack mode, it holds a positive number of rows per pack.
   * In non-pack mode the value is 0 (it means default fetch size).
   */
  protected int myPackLimit = 0;

  @Nullable
  protected ResultSet myDefaultResultSet;

  protected boolean myDefaultResultSetHasRows;

  protected int myAffectedRowsCount;

  @Nullable
  protected JdbcIntermediateCursor<?> myDefaultCursor;


  //// CONSTRUCTORS \\\\


  protected JdbcIntermediateSeance(@NotNull final JdbcIntermediateSession session,
                                   @NotNull final String statementText) {
    this.mySession = session;
    this.myStatementText = statementText;
  }


  //// IMPLEMENTATION \\\\



  @Override
  public synchronized void setInParameters(@NotNull final Object[] parameters) {
    assert myStatement != null;
    try {
      assignParameters(myStatement, parameters);
    }
    catch (SQLException sqle) {
      throw mySession.recognizeException(sqle, myStatementText);
    }
  }


  protected void assignParameters(@NotNull final PreparedStatement stmt, final Object[] params)
          throws SQLException
  {
    if (params == null) {
      return;
    }
    for (int i = 0; i < params.length; i++) {
      Object param = params[i];
      JdbcParametersHandler.assignParameter(stmt, i + 1, param);
    }
  }

  @Override
  public void setPackLimit(final int packLimit) {
    if (packLimit < 0) throw new IllegalArgumentException("PackLimit is negative: " + packLimit);
    myPackLimit = packLimit;
  }


  @Override
  public synchronized void execute() {
    try {
      mySession.tuneStatement(myStatement, myPackLimit);
      boolean gotResultSet = myStatement.execute();
      if (gotResultSet)  {
        myDefaultResultSet = mySession.getDefaultResultSet(myStatement);
        if (!JdbcUtil.isClosed(myDefaultResultSet)) {
          mySession.tuneResultSet(myDefaultResultSet, myPackLimit);
          myDefaultResultSetHasRows = myDefaultResultSet.next();  // download first rows
          if (!myDefaultResultSetHasRows) {
            JdbcUtil.close(myDefaultResultSet); // close it if it has no rows
          }
        }
        else {
          myDefaultResultSetHasRows = false;
        }
      }
      else {
        myAffectedRowsCount = myStatement.getUpdateCount();
      }
    }
    catch (SQLException sqle) {
      throw mySession.recognizeException(sqle, myStatementText);
    }
  }


  @Override
  public int getAffectedRowsCount() {
    return myAffectedRowsCount;
  }


  protected <R> JdbcIntermediateCursor<R> openDefaultCursor(final @NotNull ResultLayout<R> layout) {
    if (myDefaultResultSet == null) {
      throw new IllegalStateException("Cannot open cursor: the statement was not executed or it has not returned cursor.");
    }

    if (myDefaultCursor == null) {
      JdbcIntermediateCursor<R> cursor =
          new JdbcIntermediateCursor<R>(this, myDefaultResultSet, layout,
                                              true, myDefaultResultSetHasRows ? Boolean.TRUE : Boolean.FALSE);
      myDefaultCursor = cursor;
      return cursor;
    }
    else {
      if (layout.equals(myDefaultCursor.myResultLayout)) {
        //noinspection unchecked
        return (JdbcIntermediateCursor<R>) myDefaultCursor;
      }
      else {
        throw new IllegalStateException("The cursor already opened with another layout.");
      }
    }
  }


  @Override
  public synchronized void close() {
    if (myDefaultCursor != null && myDefaultCursor.isOpened()) {
      myDefaultCursor.close();
      myDefaultCursor = null;
    }

    if (myDefaultResultSet != null) {
      try {
        JdbcUtil.close(myDefaultResultSet);
      }
      finally {
        myDefaultResultSet = null;
      }
    }

    if (myStatement != null) {
      try {
        JdbcUtil.close(myStatement);
      }
      finally {
        myStatement = null;
      }
    }
  }


  //// DIAGNOSTIC METHODS \\\\



  @Nullable
  @Override
  public <I> I getSpecificService(@NotNull final Class<I> serviceClass,
                                  @NotNull @MagicConstant(valuesFromClass = Names.class) final String serviceName) throws ClassCastException {
    if (serviceName.equalsIgnoreCase(Names.JDBC_STATEMENT)) {
      return castTo(serviceClass, myStatement);
    }
    else if (serviceName.equalsIgnoreCase(Names.JDBC_RESULT_SET)) {
      return castTo(serviceClass, myDefaultResultSet);
    }
    else if (serviceName.equalsIgnoreCase(Names.JDBC_METADATA)) {
      return castTo(serviceClass, getResultSetMetaData());
    }
    else {
      return null;
    }
  }

  @Nullable
  private ResultSetMetaData getResultSetMetaData() {
    final ResultSet rset = myDefaultResultSet;
    if (rset == null) return null;
    try {
      return rset.getMetaData();
    }
    catch (SQLException e) {
      String className = rset.getClass().getName();
      throw mySession.recognizeException(e, className+".getMetaData()");
    }
  }


  public boolean isStatementOpened() {
    try {
      return myStatement != null
          && JdbcUtil.isClosed(myStatement);
    }
    catch (SQLException sqle) {
      JdbcUtil.printCloseException(sqle, myStatement.getClass());
      return false;
    }
  }

  public int countOpenedCursors() {
    return myDefaultCursor != null && myDefaultCursor.isOpened() ? 1 : 0;
  }

}

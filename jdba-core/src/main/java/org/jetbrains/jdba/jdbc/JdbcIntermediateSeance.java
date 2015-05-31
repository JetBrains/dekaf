package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.ResultLayout;
import org.jetbrains.jdba.exceptions.UnhandledTypeException;
import org.jetbrains.jdba.intermediate.IntegralIntermediateSeance;

import java.sql.*;



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
      assignParameter(stmt, i + 1, param);
    }
  }


  protected void assignParameter(@NotNull final PreparedStatement stmt,
                                 final int index,
                                 @Nullable final Object object)
          throws SQLException
  {
    if (object == null) {
      stmt.setNull(index, Types.BIT);
    }
    else if (object instanceof Boolean) {
      stmt.setBoolean(index, (Boolean)object);
    }
    else if (object instanceof Byte) {
      stmt.setByte(index, (Byte)object);
    }
    else if (object instanceof Short) {
      stmt.setShort(index, (Short)object);
    }
    else if (object instanceof Integer) {
      stmt.setInt(index, (Integer)object);
    }
    else if (object instanceof Float) {
      stmt.setFloat(index, (Float)object);
    }
    else if (object instanceof Double) {
      stmt.setDouble(index, (Double)object);
    }
    else if (object instanceof Long) {
      stmt.setLong(index, (Long)object);
    }
    else if (object instanceof Character) {
      stmt.setString(index, object.toString());
    }
    else if (object instanceof String) {
      stmt.setString(index, (String)object);
    }
    else if (object instanceof java.sql.Date) {
      stmt.setDate(index, (java.sql.Date)object);
    }
    else if (object instanceof java.sql.Timestamp) {
      stmt.setTimestamp(index, (java.sql.Timestamp)object);
    }
    else if (object instanceof java.sql.Time) {
      stmt.setTime(index, (java.sql.Time)object);
    }
    else if (object instanceof java.util.Date) {
      stmt.setTimestamp(index, new Timestamp(((java.util.Date)object).getTime()));
    }
    else if (object instanceof byte[]) {
      stmt.setBytes(index, (byte[])object);
    }
    else {
      boolean assigned = assignSpecificParameter(stmt, index, object);
      if (!assigned) {
        throw new UnhandledTypeException("I don't know how to pass an instance of class " +
                                                 object.getClass().getSimpleName() + " as the " +
                                                 index + "th parameter into a SQL statement.", null);
      }
    }
  }

  protected boolean assignSpecificParameter(@NotNull final PreparedStatement stmt,
                                            final int index,
                                            @NotNull final Object object) throws SQLException {
    return false;
  }


  @Override
  public synchronized void execute() {
    try {
      boolean gotResultSet = myStatement.execute();
      if (gotResultSet)  {
        myDefaultResultSet = mySession.getDefaultResultSet(myStatement);
        mySession.tuneResultSet(myDefaultResultSet);
        myDefaultResultSetHasRows = myDefaultResultSet.next();  // download first rows
        if (!myDefaultResultSetHasRows)  {
          myDefaultResultSet.close(); // close it if it has no rows
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
      JdbcIntermediateCursor<R> cursor = new JdbcIntermediateCursor<R>(this, myDefaultResultSet, layout,
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
        if (!myDefaultResultSet.isClosed()) {
          myDefaultResultSet.close();
        }
      }
      catch (SQLException sqle) {
        // TODO log somehow
      }
      finally {
        myDefaultResultSet = null;
      }
    }

    if (myStatement != null) {
      try {
        if (!myStatement.isClosed()) {
          myStatement.close();
        }
      }
      catch (SQLException sqle) {
        // TODO log somehow
      }
      finally {
        myStatement = null;
      }
    }
  }


  //// DIAGNOSTIC METHODS \\\\

  public boolean isStatementOpened() {
    try {
      return myStatement != null
          && !myStatement.isClosed();
    }
    catch (SQLException sqle) {
      // TODO log somehow
      return false;
    }
  }

  public int countOpenedCursors() {
    return myDefaultCursor != null && myDefaultCursor.isOpened() ? 1 : 0;
  }

}

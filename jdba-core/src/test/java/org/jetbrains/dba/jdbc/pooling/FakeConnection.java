package org.jetbrains.dba.jdbc.pooling;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class FakeConnection implements Connection {

  //// OUR COMMON STUFF \\\\

  private static final AtomicInteger ourConnectionSequence = new AtomicInteger();
  private static final AtomicInteger ourSavepointSequence = new AtomicInteger();


  //// OUR LITTLE STATE \\\\

  final int myNumber = ourConnectionSequence.incrementAndGet();

  boolean myAutoCommit = true;
  boolean myClosed = false;
  int myHoldability = 0;




  //// INNER CLASSES \\\\

  private class FakeSavepoint implements Savepoint {
    private final int id = ourSavepointSequence.incrementAndGet();
    private final String name = "FakeSavepoint-"+myNumber+"-"+id;

    @Override
    public int getSavepointId() { return id; }

    @Override
    public String getSavepointName() { return name; }

    @Override
    public String toString() { return name; }
  }


  //// SOME METHODS WE IMPLEMENT \\\\


  @Override
  public boolean isValid(int timeout) throws SQLException {
    checkNotClosed();
    return true;
  }


  @Override
  public int getHoldability() {
    return 0;
  }


  @Override
  public void setHoldability(int holdability) throws SQLException {
    checkNotClosed();
    myHoldability = holdability;
  }


  public boolean getAutoCommit() throws SQLException {
    checkNotClosed();
    return myAutoCommit;
  }


  @Override
  public void setAutoCommit(boolean autoCommit) throws SQLException {
    checkNotClosed();
    myAutoCommit = autoCommit;
  }


  @Override
  public void commit() throws SQLException {
    checkNotClosed();
  }


  @Override
  public void rollback() throws SQLException {
    checkNotClosed();
  }


  @Override
  public Savepoint setSavepoint() throws SQLException {
    checkNotClosed();
    return new FakeSavepoint();
  }


  @Override
  public Savepoint setSavepoint(String name) throws SQLException {
    checkNotClosed();
    // TODO implement FakeConnection.setSavepoint()
    throw new RuntimeException("Method FakeConnection.setSavepoint() is not implemented yet.");
  }


  @Override
  public void rollback(Savepoint savepoint) throws SQLException {
    checkNotClosed();
  }


  @Override
  public void releaseSavepoint(Savepoint savepoint) {
  }


  @Override
  public String nativeSQL(String sql) throws SQLException {
    checkNotClosed();
    return sql;
  }


  @Override
  public boolean isClosed() {
    return myClosed;
  }


  @Override
  public void close() throws SQLException {
    checkNotClosed();
    myClosed = true;
  }


  private void checkNotClosed() throws SQLException {
    if (myClosed) throw new SQLException("The fake connection is already closed.");
  }


  @SuppressWarnings({"SpellCheckingInspection", "unchecked"})
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    if (iface.isAssignableFrom(FakeConnection.class)) return (T)this;
    throw new SQLException("Cannot cast FakeConnection to "+iface.getName());
  }


  @SuppressWarnings("SpellCheckingInspection")
  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return iface.isAssignableFrom(FakeConnection.class);
  }


  @Override
  public String toString() {
    return "FakeConnection-"+myNumber;
  }


  @Override
  public int hashCode() {
    return myNumber;
  }




  //// NON-IMPLEMENTED METHODS \\\\


  @Override
  public Statement createStatement() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.createStatement() is not implemented yet.");
  }


  @Override
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.prepareStatement() is not implemented yet.");
  }


  @Override
  public CallableStatement prepareCall(String sql) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.prepareCall() is not implemented yet.");
  }


  @Override
  public DatabaseMetaData getMetaData() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.getMetaData() is not implemented yet.");
  }


  @Override
  public void setReadOnly(boolean readOnly) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.setReadOnly() is not implemented yet.");
  }


  @Override
  public boolean isReadOnly() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.isReadOnly() is not implemented yet.");
  }


  @Override
  public void setCatalog(String catalog) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.setCatalog() is not implemented yet.");
  }


  @Override
  public String getCatalog() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.getCatalog() is not implemented yet.");
  }


  @Override
  public void setTransactionIsolation(int level) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.setTransactionIsolation() is not implemented yet.");
  }


  @Override
  public int getTransactionIsolation() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.getTransactionIsolation() is not implemented yet.");
  }


  @Override
  public SQLWarning getWarnings() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.getWarnings() is not implemented yet.");
  }


  @Override
  public void clearWarnings() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.clearWarnings() is not implemented yet.");
  }


  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.createStatement() is not implemented yet.");
  }


  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.prepareStatement() is not implemented yet.");
  }


  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.prepareCall() is not implemented yet.");
  }


  @Override
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.getTypeMap() is not implemented yet.");
  }


  @Override
  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.setTypeMap() is not implemented yet.");
  }


  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.createStatement() is not implemented yet.");
  }


  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
    throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.prepareStatement() is not implemented yet.");
  }


  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
    throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.prepareCall() is not implemented yet.");
  }


  @Override
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.prepareStatement() is not implemented yet.");
  }


  @Override
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.prepareStatement() is not implemented yet.");
  }


  @Override
  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.prepareStatement() is not implemented yet.");
  }


  @Override
  public Clob createClob() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.createClob() is not implemented yet.");
  }


  @Override
  public Blob createBlob() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.createBlob() is not implemented yet.");
  }


  @Override
  public NClob createNClob() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.createNClob() is not implemented yet.");
  }


  @Override
  public SQLXML createSQLXML() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.createSQLXML() is not implemented yet.");
  }


  @Override
  public void setClientInfo(String name, String value) throws SQLClientInfoException {
    throw new RuntimeException("Method FakeConnection.setClientInfo() is not implemented yet.");
  }


  @Override
  public void setClientInfo(Properties properties) throws SQLClientInfoException {
    throw new RuntimeException("Method FakeConnection.setClientInfo() is not implemented yet.");
  }


  @Override
  public String getClientInfo(String name) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.getClientInfo() is not implemented yet.");
  }


  @Override
  public Properties getClientInfo() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.getClientInfo() is not implemented yet.");
  }


  @Override
  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.createArrayOf() is not implemented yet.");
  }


  @Override
  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.createStruct() is not implemented yet.");
  }


  //@Override java8
  public void setSchema(String schema) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.setSchema() is not implemented yet.");
  }


  //@Override java8
  public String getSchema() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.getSchema() is not implemented yet.");
  }


  //@Override java8
  public void abort(Executor executor) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.abort() is not implemented yet.");
  }


  //@Override java8
  public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.setNetworkTimeout() is not implemented yet.");
  }


  //@Override java8
  public int getNetworkTimeout() throws SQLException {
    checkNotClosed();
    throw new RuntimeException("Method FakeConnection.getNetworkTimeout() is not implemented yet.");
  }
}

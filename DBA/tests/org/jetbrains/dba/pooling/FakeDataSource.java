package org.jetbrains.dba.pooling;

import testing.threads.ThreadTestUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class FakeDataSource implements DataSource {


  private PrintWriter myPrintWriter = new PrintWriter(System.err);

  private AtomicInteger myGotCounter = new AtomicInteger();


  @Override
  public Connection getConnection() {
    ThreadTestUtils.sleep(15);
    myGotCounter.incrementAndGet();
    return new FakeConnection();
  }


  @Override
  public Connection getConnection(String username, String password) {
    ThreadTestUtils.sleep(25);
    myGotCounter.incrementAndGet();
    return new FakeConnection();
  }


  @Override
  public PrintWriter getLogWriter() {
    return myPrintWriter;
  }


  @Override
  public void setLogWriter(PrintWriter out) {
    myPrintWriter = out;
  }


  @Override
  public void setLoginTimeout(int seconds) {
  }


  @Override
  public int getLoginTimeout() {
    return 1;
  }


  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new RuntimeException("Method FakeDataSource.unwrap() is not implemented.");
  }


  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }

  public int countGotConnections() {
    return myGotCounter.get();
  }

  public void reset() {
    myGotCounter.set(0);
  }


  //@Override java.8
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException("Method FakeDataSource.getParentLogger() is not implemented.");
  }
}

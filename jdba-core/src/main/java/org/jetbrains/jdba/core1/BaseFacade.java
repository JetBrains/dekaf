package org.jetbrains.jdba.core1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.exceptions.DBIsNotConnected;
import org.jetbrains.jdba.inter.DBErrorRecognizer;
import org.jetbrains.jdba.jdbc.pooling.ConnectionPool;
import org.jetbrains.jdba.sql.SQL;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseFacade implements DBFacade {

  @NotNull
  private final Rdbms myRdbms;

  @NotNull
  private final ConnectionPool myPool;

  @NotNull
  private final AtomicInteger mySessionCounter = new AtomicInteger();

  @NotNull
  private final ConcurrentLinkedQueue<BaseSession> mySessions =
      new ConcurrentLinkedQueue<BaseSession>();

  @NotNull
  protected final SQL mySQL;

  @NotNull
  protected final DBErrorRecognizer myErrorRecognizer;



  public BaseFacade(@NotNull final Rdbms rdbms,
                    @NotNull final DataSource source,
                    @NotNull final DBErrorRecognizer recognizer,
                    @NotNull final SQL sql) {
    myRdbms = rdbms;
    myPool = new ConnectionPool(source);
    myErrorRecognizer = recognizer;
    mySQL = sql;
  }


  public void setSessionsLimit(int sessionsLimit) {
    myPool.setConnectionsLimit(sessionsLimit);
  }


  @NotNull
  @Override
  public Rdbms rdbms() {
    return myRdbms;
  }


  @NotNull
  public SQL sql() {
    return mySQL;
  }


  public void connect() {
    try {
      myPool.connect();
    }
    catch (SQLException e) {
      throw myErrorRecognizer.recognizeError(e, "<connect>");
    }
  }


  @NotNull
  protected BaseSession createSessionForConnection(@NotNull final Connection connection) {
    return new BaseSession(this, connection, true);
  }



  public void reconnect() {
    myPool.disconnect();
    connect();
  }


  public void disconnect() {
    myPool.disconnect();
  }


  public boolean isConnected() {
    return myPool.isReady();
  }


  int takeNextSessionNumber() {
    return mySessionCounter.incrementAndGet();
  }


  public <R> R inSession(final InSession<R> operation) {
    if (!myPool.isReady())
      throw new DBIsNotConnected("Facade is not connected.");

    try {
      Connection connection = myPool.borrow();
      try {
        BaseSession session =
          createSessionForConnection(connection);
        mySessions.add(session);
        try {
          return operation.run(session);
        }
        finally {
          mySessions.remove(session);
        }
      }
      finally {
        myPool.release(connection);
      }
    }
    catch (SQLException e) {
      throw myErrorRecognizer.recognizeError(e, "<prepare session>");
    }
  }


  public void inSession(final InSessionNoResult operation) {
    inSession(new InSession<Void>() {
      @Override
      public Void run(@NotNull DBSession session) {
        operation.run(session);
        return null;
      }
    });
  }


  public <R> R inTransaction(final InTransaction<R> operation) {
    return inSession(new InSession<R>() {
      @Override
      public R run(@NotNull DBSession session) {
        return session.inTransaction(new InTransaction<R>() {
          @Override
          public R run(@NotNull DBTransaction tran) {
            return operation.run(tran);
          }
        });
      }
    });
  }


  public void inTransaction(final InTransactionNoResult operation) {
    inSession(new InSession<Void>() {
      @Override
      public Void run(@NotNull DBSession session) {
        session.inTransaction(new InTransaction<Void>() {
          @Override
          public Void run(@NotNull DBTransaction tran) {
            operation.run(tran);
            return null;
          }
        });
        return null;
      }
    });
  }


  @NotNull
  public DBErrorRecognizer getErrorRecognizer() {
    return myErrorRecognizer;
  }
}

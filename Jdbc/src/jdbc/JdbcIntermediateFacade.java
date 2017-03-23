package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.exceptions.DBException;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateFacade;
import org.jetbrains.dekaf.jdbc.pooling.ConnectionPool;
import org.jetbrains.dekaf.jdbc.pooling.SimpleDataSource;
import org.jetbrains.dekaf.util.Version;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jetbrains.dekaf.util.Objects.castTo;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcIntermediateFacade implements IntegralIntermediateFacade {

  //// STATE \\\\

  @NotNull
  protected final ConnectionPool myPool;

  @NotNull
  protected final DBExceptionRecognizer myExceptionRecognizer;

  private final LinkedBlockingQueue<JdbcIntermediateSession> mySessions =
          new LinkedBlockingQueue<JdbcIntermediateSession>();


  //// CONSTRUCTORS \\\\

  public JdbcIntermediateFacade(@NotNull final String connectionString,
                                @Nullable final Properties connectionProperties,
                                @NotNull final Driver driver,
                                int connectionsLimit,
                                @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    this(prepareDataSource(connectionString, connectionProperties, driver),
         connectionsLimit,
         exceptionRecognizer);
  }

  public JdbcIntermediateFacade(@NotNull final DataSource dataSource,
                                int connectionsLimit,
                                @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    myPool = new ConnectionPool(dataSource);
    myPool.setConnectionsLimit(connectionsLimit);
    myExceptionRecognizer = exceptionRecognizer;
  }

  @NotNull
  private static DataSource prepareDataSource(final @NotNull String connectionString,
                                              final @Nullable Properties connectionProperties,
                                              final @NotNull Driver driver) {
    final SimpleDataSource dataSource = new SimpleDataSource(connectionString,
                                                             connectionProperties,
                                                             driver);
    dataSource.setLogWriter(new PrintWriter(System.out));
    return dataSource;
  }


  //// IMPLEMENTATION \\\\

  @NotNull
  @Override
  public Rdbms rdbms() {
    return UnknownDatabase.RDBMS;
  }


  @Override
  public synchronized void connect() {
    try {
      myPool.connect();
    }
    catch (SQLException sqle) {
      throw  myExceptionRecognizer.recognizeException(sqle, "connect");
    }
  }

  @Override
  public synchronized void reconnect() {
    // TODO implement JdbcInterFacade.reconnect
    throw new RuntimeException("The JdbcInterFacade.reconnect has not been implemented yet.");
  }

  @Override
  public synchronized void disconnect() {
    try {
      while (!mySessions.isEmpty()) {
        Thread.sleep(10);
        //noinspection MismatchedQueryAndUpdateOfCollection
        Collection<JdbcIntermediateSession> sessionsToClose = new ArrayList<JdbcIntermediateSession>(10);
        mySessions.drainTo(sessionsToClose, 10);
        for (JdbcIntermediateSession sessionToClose : sessionsToClose) {
          sessionToClose.close();
        }
      }
    }
    catch (InterruptedException ie) {
      // do nothing
    }

    myPool.disconnect();
  }

  @Override
  public boolean isConnected() {
    return myPool.isReady();
  }

  @NotNull
  @Override
  public ConnectionInfo getConnectionInfo() {
    ConnectionInfo info = null;
    try {
      info = obtainConnectionInfoNatively();
    }
    catch (DBException e) {
      // TODO log the exception if in debug mode
    }

    if (info == null) {
      info = obtainConnectionInfoFromJdbc();
    }

    return info;
  }

  @Nullable
  protected ConnectionInfo obtainConnectionInfoNatively() {
    return null; // inheritors should override this method
  }

  @NotNull
  protected ConnectionInfo obtainConnectionInfoFromJdbc() {
    try {
      Connection connection = myPool.borrow();
      try {
        DatabaseMetaData md = connection.getMetaData();
        String rdbmsName = md.getDatabaseProductName();
        if (rdbmsName == null) rdbmsName = connection.getClass().getName();
        String databaseName = connection.getCatalog();
        String schemaName = getSchema(connection);
        String userName = md.getUserName();
        Version serverVersion = Version.of(md.getDatabaseMajorVersion(), md.getDatabaseMinorVersion());
        Version driverVersion = Version.of(md.getDriverMajorVersion(), md.getDriverMinorVersion());
        return new ConnectionInfo(rdbmsName,
                                  databaseName, schemaName, userName,
                                  serverVersion, driverVersion);
      }
      finally {
        myPool.release(connection);
      }
    }
    catch (SQLException sqle) {
      throw  myExceptionRecognizer.recognizeException(sqle, "getting brief connection info using JDBC connection metadata");
    }
  }

  /**
   * Try to call the "Connection#getSchema()" function
   * that appears in JRE 1.7.
   *
   * Some JDBC vendor can also ignore this method or throw exceptions.
   */
  @Nullable
  private static String getSchema(final Connection connection) {
    String schemaName = null;
    try {
      final Class<? extends Connection> connectionClass = connection.getClass();
      final Method getSchemaMethod = connectionClass.getMethod("getSchema");
      if (getSchemaMethod != null) {
        schemaName = (String) getSchemaMethod.invoke(connection);
      }
    }
    catch (NoSuchMethodException nsm) {
      // no such method. sad
    }
    catch (Exception e) {
      // TODO log this somehow?
    }
    return schemaName;
  }

  @NotNull
  @Override
  public JdbcIntermediateSession openSession() {
    final Connection connection;
    try {
      connection = myPool.borrow();
    }
    catch (SQLException sqle) {
      throw  myExceptionRecognizer.recognizeException(sqle, "borrow a connection from the pool");
    }

    final JdbcIntermediateSession session = instantiateSession(connection, false);
    mySessions.add(session);
    return session;
  }

  @NotNull
  protected JdbcIntermediateSession instantiateSession(@NotNull final Connection connection,
                                                       final boolean ownConnection) {
    return new JdbcIntermediateSession(this, myExceptionRecognizer, connection, ownConnection);
  }

  @NotNull
  public DBExceptionRecognizer getExceptionRecognizer() {
    return myExceptionRecognizer;
  }

  void sessionIsClosed(@NotNull final JdbcIntermediateSession session, @NotNull final Connection connection) {
    mySessions.remove(session);
    myPool.release(connection);
  }

  @Nullable
  @Override
  public <I> I getSpecificService(@NotNull final Class<I> serviceClass,
                                  @NotNull final String serviceName) throws ClassCastException {
    if (serviceName.equalsIgnoreCase(Names.INTERMEDIATE_SERVICE)) {
      return castTo(serviceClass, this);
    }
    else {
      return myPool.getSpecificService(serviceClass, serviceName);
    }
  }



  @NotNull
  protected ConnectionInfo getConnectionInfoSmartly(final String envQuery,
                                                    final Pattern serverVersionPattern,
                                                    final int serverVersionGroupIndex,
                                                    final Pattern driverVersionPattern,
                                                    final int driverVersionGroupIndex) {
    String[] env;
    Version serverVersion, driverVersion;

    final JdbcIntermediateSession session = openSession();
    try {
      // environment
      env = session.queryOneRow(envQuery, 3, String.class);

      if (env == null) env = new String[] {null,null,null};
      assert env.length == 3 : "Session info should contain 3 components";

      // versions
      String rdbmsName, serverVersionStr, driverVersionStr;
      try {
        DatabaseMetaData md = session.getConnection().getMetaData();
        rdbmsName = md.getDatabaseProductName();
        if (rdbmsName == null) rdbmsName = session.getConnection().getClass().getName();
        serverVersionStr = md.getDatabaseProductVersion();
        driverVersionStr = md.getDriverVersion();
      }
      catch (SQLException sqle) {
        throw getExceptionRecognizer().recognizeException(sqle, "getting versions using JDBC metadata");
      }

      serverVersion =
          extractVersion(serverVersionStr, serverVersionPattern, serverVersionGroupIndex);
      driverVersion =
          extractVersion(driverVersionStr, driverVersionPattern, driverVersionGroupIndex);

      // ok
      return new ConnectionInfo(rdbmsName, env[0], env[1], env[2], serverVersion, driverVersion);
    }
    finally {
      session.close();
    }
  }

  protected static final Pattern SIMPLE_VERSION_PATTERN =
      Pattern.compile("(\\d{1,2}(\\.\\d{1,3}){1,5})");

  @NotNull
  protected static Version extractVersion(@Nullable final String serverVersionStr,
                                          @NotNull final Pattern versionPattern,
                                          final int groupIndex) {
    if (serverVersionStr == null || serverVersionStr.isEmpty()) return Version.ZERO;
    Matcher m = versionPattern.matcher(serverVersionStr);
    if (m.find()) return Version.of(m.group(groupIndex));
    else return Version.ZERO;
  }


  //// DIAGNOSTIC METHODS \\\\

  public int countOpenedSessions() {
    return mySessions.size();
  }

  public int countOpenedConnections() {
    return myPool.countAllConnections();
  }

  public int countOpenedSeances() {
    int count = 0;
    for (JdbcIntermediateSession session : mySessions) count += session.countOpenedSeances();
    return count;
  }

  public int countOpenedCursors() {
    int count = 0;
    for (JdbcIntermediateSession session : mySessions) count += session.countOpenedCursors();
    return count;
  }
}

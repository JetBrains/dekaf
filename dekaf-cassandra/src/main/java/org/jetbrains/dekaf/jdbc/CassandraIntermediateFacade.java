package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Cassandra;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;



/**
 * @author Liudmila Kornilova
 **/
public class CassandraIntermediateFacade extends JdbcIntermediateFacade {

  public CassandraIntermediateFacade(@NotNull final String connectionString,
                                     @Nullable final Properties connectionProperties,
                                     @NotNull final Driver driver,
                                     final int connectionsLimit,
                                     @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(connectionString, connectionProperties, driver, connectionsLimit, exceptionRecognizer);
  }

  public CassandraIntermediateFacade(@NotNull final DataSource dataSource,
                                     final int connectionsLimit,
                                     boolean ownConnections,
                                     @NotNull final DBExceptionRecognizer exceptionRecognizer) {
    super(dataSource, connectionsLimit, ownConnections, exceptionRecognizer);
  }

  @NotNull
  @Override
  public Rdbms rdbms() {
    return Cassandra.RDBMS;
  }

  @NotNull
  @Override
  protected JdbcIntermediateSession instantiateSession(@NotNull Connection connection,
                                                       boolean ownConnection) {
    return new CassandraIntermediateSession(this, myExceptionRecognizer, connection, ownConnection);
  }

}

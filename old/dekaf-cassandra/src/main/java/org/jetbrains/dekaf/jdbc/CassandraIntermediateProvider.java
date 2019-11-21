package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Cassandra;
import org.jetbrains.dekaf.Rdbms;

import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Pattern;



public class CassandraIntermediateProvider extends JdbcIntermediateRdbmsProvider {


  //// SETTINGS AND STATE \\\\

  public final static CassandraIntermediateProvider INSTANCE =
      new CassandraIntermediateProvider();


  static final Pattern CASSANDRA_CONNECTION_STRING_PATTERN =
      Pattern.compile("^jdbc:cassandra:.+$");

  static final String CASSANDRA_CONNECTION_STRING_EXAMPLE =
      "jdbc:cassandra://localhost:8123";


  //// INITIALIZATION \\\\

  @NotNull
  @Override
  protected String getConnectionStringExample() {
    return CASSANDRA_CONNECTION_STRING_EXAMPLE;
  }

  @Override
  protected Driver loadDriver(final String connectionString) {
    return null;
  }



  //// IMPLEMENTATION \\\\



  @NotNull
  @Override
  public Rdbms rdbms() {
    return Cassandra.RDBMS;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return CASSANDRA_CONNECTION_STRING_PATTERN;
  }

  @Override
  public byte specificity() {
    return SPECIFICITY_NATIVE;
  }


  @NotNull
  @Override
  protected CassandraIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                                          @Nullable final Properties connectionProperties,
                                                          final int connectionsLimit,
                                                          @NotNull final Driver driver) {
    return new CassandraIntermediateFacade(connectionString,
                                           connectionProperties,
                                           driver,
                                           connectionsLimit,
                                           CassandraExceptionRecognizer.INSTANCE);
  }

  @NotNull
  @Override
  public BaseExceptionRecognizer getExceptionRecognizer() {
    return CassandraExceptionRecognizer.INSTANCE;
  }

}

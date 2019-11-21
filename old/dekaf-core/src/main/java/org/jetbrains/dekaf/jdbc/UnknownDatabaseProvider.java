package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;

import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class UnknownDatabaseProvider extends JdbcIntermediateRdbmsProvider {


  private static final Pattern CONNECTION_STRING_PATTERN =
          Pattern.compile("^jdbc:.+$");

  @NotNull
  @Override
  public Rdbms rdbms() {
    return UnknownDatabase.RDBMS;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return CONNECTION_STRING_PATTERN;
  }

  @Override
  public byte specificity() {
    return SPECIFICITY_UNSPECIFIC;
  }

  @Nullable
  @Override
  protected String getConnectionStringExample() {
    return null;
  }

  @Nullable
  @Override
  protected Driver loadDriver(final String connectionString) {
    return null;
  }

  @NotNull
  @Override
  public BaseExceptionRecognizer getExceptionRecognizer() {
    return UnknownDatabaseExceptionRecognizer.INSTANCE;
  }

  @NotNull
  @Override
  protected JdbcIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                                     @Nullable final Properties connectionProperties,
                                                     final int connectionsLimit,
                                                     @NotNull final Driver driver) {
    return new UnknownDatabaseIntermediateFacade(connectionString,
                                                 connectionProperties,
                                                 driver,
                                                 connectionsLimit,
                                                 getExceptionRecognizer());
  }

}

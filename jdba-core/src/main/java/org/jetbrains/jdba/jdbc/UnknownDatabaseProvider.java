package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;

import java.sql.Driver;
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
  protected Driver loadDriver() {
    return null;
  }

  @NotNull
  @Override
  public BaseExceptionRecognizer getExceptionRecognizer() {
    return UnknownDatabaseExceptionRecognizer.INSTANCE;
  }
}

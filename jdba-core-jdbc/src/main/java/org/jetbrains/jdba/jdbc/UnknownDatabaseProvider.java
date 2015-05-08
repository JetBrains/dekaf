package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;

import java.sql.Driver;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class UnknownDatabaseProvider extends JdbcIntermediateRdbmsProvider {

  public static final UnknownDatabaseProvider INSTANCE = new UnknownDatabaseProvider();

  private static final Pattern CONNECTION_STRING_PATTERN =
          Pattern.compile("^jdbc:.+$");

  private UnknownDatabaseProvider() {}

  @Override
  public Rdbms rdbms() {
    return UnknownDatabase.RDBMS;
  }

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
  Driver loadDriver() {
    return null;
  }

  @Override
  public BaseErrorRecognizer getErrorRecognizer() {
    return UnknownDatabaseErrorRecognizer.INSTANCE;
  }
}

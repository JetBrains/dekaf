package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.Rdbms;

import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class UnknownDatabaseServiceProvider extends JdbcInterBaseServiceProvider {

  public static final UnknownDatabaseServiceProvider INSTANCE = new UnknownDatabaseServiceProvider();

  private static final Pattern CONNECTION_STRING_PATTERN =
          Pattern.compile("^jdbc:.+$");

  private UnknownDatabaseServiceProvider() {}

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

  @Override
  public BaseErrorRecognizer getErrorRecognizer() {
    return UnknownDatabaseErrorRecognizer.INSTANCE;
  }
}

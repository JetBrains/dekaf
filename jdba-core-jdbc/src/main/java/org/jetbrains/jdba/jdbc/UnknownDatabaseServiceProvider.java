package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.Rdbms;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class UnknownDatabaseServiceProvider extends JdbcInterBaseServiceProvider {

  public static final UnknownDatabaseServiceProvider INSTANCE = new UnknownDatabaseServiceProvider();

  private UnknownDatabaseServiceProvider() {}

  @Override
  public Rdbms rdbms() {
    return UnknownDatabase.RDBMS;
  }

  @Override
  public BaseErrorRecognizer getErrorRecognizer() {
    return UnknownDatabaseErrorRecognizer.INSTANCE;
  }
}

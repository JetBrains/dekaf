package org.jetbrains.jdba.core;

/**
 * @author Leonid Bushuev from JetBrains
 */
public interface DBCommandRunner {

  /**
   * Assigns command parameters.
   * @param params parameters, one for each '?' sign in the command.
   * @return the query.
   */
  DBCommandRunner withParams(Object... params);

  /**
   * Performs the query.
   */
  DBCommandRunner run();
}


package org.jetbrains.dba.core;

/**
 * @author Leonid Bushuev from JetBrains
 */

public interface DBQueryRunner<S> {

  /**
   * Assigns query parameters.
   * @param params parameters, one for each '?' sign in the query.
   * @return the query.
   */
  DBQueryRunner<S> withParams(Object... params);

  /**
   * Performs the query and fetches the resulting cursor.
   * @return fetch result.
   */
  S run();
}

package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface DBQueryRunner<S> {

  /**
   * Assigns query parameters.
   * @param params parameters, one for each '?' sign in the query.
   * @return itself.
   */
  @NotNull
  DBQueryRunner<S> withParams(Object... params);

  /**
   * Switches to pack mode.
   * In this mode, call to the {@link #run()} methods returns the first pack only.
   * @param rowsPerPack rows per pack.
   * @return itself.
   * @see #run()
   * @see #nextPack()
   */
  @NotNull
  DBQueryRunner<S> packBy(int rowsPerPack);

  /**
   * Performs the query and fetches the resulting cursor and returns the fetched result.
   * @return fetch result.
   */
  S run();

  /**
   * Fetches and returns the next pack if is in the pack mode.
   * @return next pack of the data, or null if all data already fetched.
   * @see #packBy(int)
   */
  S nextPack();

}

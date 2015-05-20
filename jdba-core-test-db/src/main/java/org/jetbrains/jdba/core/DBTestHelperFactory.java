package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Rdbms;

import java.util.Set;



/**
 * Creates applicable test helpers.
 *
 * @author Leonid Bushuev from JetBrains
 **/
public interface DBTestHelperFactory {

  /**
   * RDBMS that this factory supports.
   * @return the set of supported RDBMS.
   */
  @NotNull
  Set<Rdbms> supportRdbms();


  /**
   * Creates a helper for the specified RDBMS.
   *
   * <p>
   *   This method creates a separate instance on each call.
   * </p>
   *
   * @param facade the facade of database to create a helper for.
   * @return       just created helper.
   */
  @NotNull
  DBTestHelper createTestHelperFor(@NotNull DBFacade facade);

}

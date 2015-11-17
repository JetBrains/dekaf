package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.Rdbms;



/**
 * Unknown/unspecified RDBMS declaration class.
 *
 * @author Leonid Bushuev from JetBrains
 */
public abstract class UnknownDatabase {

  /**
   * Unknown RDBMS marker.
   */
  public static final Rdbms RDBMS = Rdbms.of("UNKNOWN");

}

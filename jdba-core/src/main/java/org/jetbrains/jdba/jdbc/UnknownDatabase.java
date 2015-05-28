package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.Rdbms;



/**
 * Unknown/unspecified RDBMS declaration class.
 *
 * @author Leonid Bushuev from JetBrains
 */
public abstract class UnknownDatabase {

  /**
   * Unknown RDBMS marker.
   */
  public static final Rdbms RDBMS = new Rdbms("UNKNOWN");

}

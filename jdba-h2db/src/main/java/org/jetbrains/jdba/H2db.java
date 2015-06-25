package org.jetbrains.jdba;

/**
 * H2 DB declaration class.
 *
 * @author Leonid Bushuev from JetBrains
 */
public abstract class H2db {

  /**
   * H2 RDBMS marker.
   */
  public final static Rdbms RDBMS = new Rdbms("H2");

}

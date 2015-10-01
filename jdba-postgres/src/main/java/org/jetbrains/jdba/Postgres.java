package org.jetbrains.jdba;

/**
 * PostgreSQL declaration class.
 *
 * @author Leonid Bushuev from JetBrains
 */
public abstract class Postgres {

  /**
   * PostgreSQL RDBMS marker.
   */
  public final static Rdbms RDBMS = new Rdbms("POSTGRES");

}

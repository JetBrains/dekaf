package org.jetbrains.jdba.postgre;

import org.jetbrains.jdba.Rdbms;



/**
 * PostgreSQL declaration class.
 *
 * @author Leonid Bushuev from JetBrains
 */
public abstract class Postgre {

  /**
   * PostgreSQL RDBMS marker.
   */
  public final static Rdbms RDBMS = new Rdbms("POSTGRE");

}

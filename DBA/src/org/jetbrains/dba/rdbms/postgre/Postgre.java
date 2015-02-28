package org.jetbrains.dba.rdbms.postgre;

import org.jetbrains.dba.Rdbms;



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

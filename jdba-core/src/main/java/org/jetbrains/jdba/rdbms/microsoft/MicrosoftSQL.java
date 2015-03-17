package org.jetbrains.jdba.rdbms.microsoft;

import org.jetbrains.jdba.Rdbms;



/**
 * Microsoft SQL Server RDBMS declaration class.
 *
 * @author Leonid Bushuev from JetBrains
 */
public abstract class MicrosoftSQL {

  /**
   * Microsoft SQL Server RDBMS marker.
   */
  public final static Rdbms RDBMS = new Rdbms("MSSQL");

}

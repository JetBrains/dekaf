package org.jetbrains.dekaf;

/**
 * MsSQL declaration class.
 *
 * @author Leonid Bushuev from JetBrains
 */
public abstract class Mssql {

  /**
   * MsSQL RDBMS marker.
   */
  public final static Rdbms RDBMS = Rdbms.of("MSSQL");

  public final static String AZURE_FLAVOUR = "Azure";
}

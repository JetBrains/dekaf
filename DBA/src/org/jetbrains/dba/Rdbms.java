package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;



/**
 * Type of RDBMS.
 *
 * @author Leonid Bushuev from JetBrains
 */
public enum Rdbms {

  /**
   * PostgreSQL.
   */
  POSTGRE ("PG"),

  /**
   * Oracle Database.
   */
  ORACLE ("ORA"),

  /**
   * Microsoft SQL Server.
   */
  MSSQL ("MSSQL"),

  /**
   * MySQL.
   */
  MYSQL ("MYSQL"),

  /**
   * HyperSonic SQL v.2.
   */
  HSQL2 ("HSQL"),

  /**
   * The RDBMS is unknown or has not been determined.
   */
  UNKNOWN ("???");


  @NotNull
  public final String shortName;



  Rdbms(@NotNull String shortName) {
    this.shortName = shortName;
  }
}

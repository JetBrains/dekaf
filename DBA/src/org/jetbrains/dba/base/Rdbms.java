package org.jetbrains.dba.base;

/**
 * Type of RDBMS.
 *
 * @author Leonid Bushuev from JetBrains
 */
public enum Rdbms {

  /**
   * PostgreSQL.
   */
  POSTGRE,

  /**
   * Oracle Database.
   */
  ORACLE,

  /**
   * Microsoft SQL Server.
   */
  MSSQL,

  /**
   * MySQL.
   */
  MYSQL,

  /**
   * HyperSonic SQL v.2.
   */
  HSQL2,

  /**
   * The RDBMS is unknown or has not been determined.
   */
  UNKNOWN

}

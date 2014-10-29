package org.jetbrains.dba;

/**
 * Well known RDBMS.
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface KnownRdbms {


  /**
   * PostgreSQL.
   */
  Rdbms POSTGRE = new Rdbms("POSTGRE");

  /**
   * Oracle Database.
   */
  Rdbms ORACLE = new Rdbms("ORACLE");

  /**
   * Microsoft SQL Server.
   */
  Rdbms MSSQL = new Rdbms("MSSQL");

  /**
   * MySQL.
   */
  Rdbms MYSQL = new Rdbms("MYSQL");

  /**
   * HyperSonic SQL v.2.
   */
  Rdbms HSQL = new Rdbms("HSQL");

  /**
   * The RDBMS is unknown or has not been determined.
   */
  Rdbms UNKNOWN = new Rdbms("UNKNOWN");

}

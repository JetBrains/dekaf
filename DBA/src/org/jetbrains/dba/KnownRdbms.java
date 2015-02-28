package org.jetbrains.dba;

/**
 * Well known RDBMS.
 *
 * @author Leonid Bushuev from JetBrains
 *
 * @deprecated use RDBMS declarations classes instead.
 * @see org.jetbrains.dba.rdbms.oracle.Oracle
 */
@Deprecated
public interface KnownRdbms {


  /**
   * PostgreSQL.
   */
  Rdbms POSTGRE = org.jetbrains.dba.rdbms.postgre.Postgre.RDBMS;

  /**
   * Oracle Database.
   */
  Rdbms ORACLE = org.jetbrains.dba.rdbms.oracle.Oracle.RDBMS;

  /**
   * Microsoft SQL Server.
   */
  Rdbms MSSQL = org.jetbrains.dba.rdbms.microsoft.MicrosoftSQL.RDBMS;

  /**
   * MySQL.
   */
  Rdbms MYSQL = org.jetbrains.dba.rdbms.mysql.MySQL.RDBMS;

  /**
   * HyperSonic SQL v.2.
   */
  Rdbms HSQL = new Rdbms("HSQL");

  /**
   * The RDBMS is unknown or has not been determined.
   */
  Rdbms UNKNOWN = new Rdbms("UNKNOWN");

}

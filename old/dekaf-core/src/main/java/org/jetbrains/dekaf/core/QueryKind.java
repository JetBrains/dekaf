package org.jetbrains.dekaf.core;

/**
 * Kind of SQL query.
 *
 * @author Leonid Bushuev from JetBrains
 */
public enum QueryKind{

  /**
   * Select query or call to a function that returns cursor.
   */
  SELECT,

  /**
   * Insert/Update/Delete query or a code block that can be performed inside a transaction.
   */
  IUD,

  /**
   * Other kind of query (DDL or DCL).
   */
  OTHER
}

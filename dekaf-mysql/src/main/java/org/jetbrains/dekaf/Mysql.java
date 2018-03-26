package org.jetbrains.dekaf;

/**
 * MySQL declaration class.
 *
 * @author Leonid Bushuev from JetBrains
 */
public abstract class Mysql {

  /**
   * MySQL RDBMS marker.
   */
  public final static Rdbms RDBMS = Rdbms.of("MYSQL");

  public final static String MYSQL_FLAVOUR = "MySQL";
  public final static String MARIADB_FLAVOUR = "MariaDB";
  public final static String MEMSQL_FLAVOUR = "MemSQL";
}

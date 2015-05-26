package org.jetbrains.jdba.core;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public interface DBTestHelper {


  /**
   * Ensures that there are no tables or view that can conflict
   * with the given names.
   * If they exist, drops them.
   *
   * @param names script names of tables or views to drop (if they exist).
   */
  void ensureNoTableOrView(String... names);


  /**
   * Prepares a table or view named <b><tt>X1000</tt></b> that contains
   * 1000 rows. It has one column named <b><tt>X</tt></b> with numbers
   * from 1 to 1000.
   */
  void prepareX1000();


  /**
   * Prepares a table or view named <b><tt>X1000000</tt></b> that contains
   * 1000000 rows. It has one column named <b><tt>X</tt></b> with numbers
   * from 1 to 1000000.
   */
  void prepareX1000000();

}

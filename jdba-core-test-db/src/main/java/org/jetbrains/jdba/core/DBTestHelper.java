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


}

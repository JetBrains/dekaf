package org.jetbrains.jdba.core1;

/**
 * @author Leonid Bushuev from JetBrains
 */
public interface DBScriptRunner {

  /**
   * Performs the script - all commands one by one.
   */
  DBScriptRunner run();
}


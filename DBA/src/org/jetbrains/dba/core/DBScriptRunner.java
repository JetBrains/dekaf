package org.jetbrains.dba.core;

/**
 * @author Leonid Bushuev from JetBrains
 */
public interface DBScriptRunner {

  /**
   * Performs the script - all commands one by one.
   */
  DBScriptRunner run();
}


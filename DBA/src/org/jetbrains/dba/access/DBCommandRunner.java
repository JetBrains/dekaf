package org.jetbrains.dba.access;

/**
 * @author Leonid Bushuev from JetBrains
 */

public interface DBCommandRunner {
  DBCommandRunner withParams(Object... params);

  DBCommandRunner run();
}

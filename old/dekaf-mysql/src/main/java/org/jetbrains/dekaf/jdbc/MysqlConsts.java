package org.jetbrains.dekaf.jdbc;

/**
 * @author Leonid Bushuev from JetBrains
 **/
public interface MysqlConsts {

  /**
   * Fetching whole result set at once when non-pack mode,
   * fetching row by row in pack mode.
   */
  byte FETCH_STRATEGY_AUTO = 0;

  /**
   * Always fetch row by row.
   */
  byte FETCH_STRATEGY_ROW = 1;

  /**
   * Always fetch all at once.
   */
  byte FETCH_STRATEGY_WHOLE = 2;

}

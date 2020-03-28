package org.jetbrains.dekaf.jdbc.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcValueGetter<V> {

  /**
   * Gets the cell value from the given result set.
   * @param rset  result set.
   * @param index column index, starting from 1 (as in JDBC).
   * @return the got value, can be null.
   * @throws SQLException
   */
  @Nullable
  abstract V getValue(@NotNull ResultSet rset, int index)
          throws SQLException;

}

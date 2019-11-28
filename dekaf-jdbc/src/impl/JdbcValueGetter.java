package org.jetbrains.dekaf.jdbc.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcValueGetter<V> {

  @Nullable
  abstract V getValue(@NotNull ResultSet rset, int index)
          throws SQLException;

}

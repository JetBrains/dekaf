package org.jetbrains.dba.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * Result set value componentGetter.
 *
 * @author Leonid Bushuev from JetBrains
 */
abstract class ValueGetter<V> {

  @Nullable
  abstract V getValue(@NotNull ResultSet rset, int index)
    throws SQLException;
}

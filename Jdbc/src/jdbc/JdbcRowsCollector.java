package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcRowsCollector<T> {

  protected boolean hasMoreRows = true;

  protected abstract T collectRows(@NotNull final ResultSet rset, int limit) throws SQLException;

}

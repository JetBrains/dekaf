package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * Fetcher that can fetch rows from {@link java.sql.ResultSet}.
 * <p>
 * Stateless service.
 * </p>
 *
 * @author Leonid Bushuev from JetBrains
 */
public abstract class RowFetcher<R> {

  void init(@NotNull final ResultSet rset)
    throws SQLException {
  }


  abstract R fetchRow(@NotNull final ResultSet rset)
    throws SQLException;
}

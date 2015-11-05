package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * Fetcher that can fetch rows from {@link ResultSet}.
 * <p>
 * Stateless service.
 * </p>
 *
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcRowFetcher<R> {


  abstract R fetchRow(@NotNull final ResultSet rset)
    throws SQLException;
}

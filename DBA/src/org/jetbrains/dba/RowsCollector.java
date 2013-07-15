package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class RowsCollector<S> {

  public abstract boolean expectManyRows();

  public abstract S collectRows(@NotNull final ResultSet rset)
    throws SQLException;
}

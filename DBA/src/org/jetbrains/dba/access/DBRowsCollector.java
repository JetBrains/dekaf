package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface DBRowsCollector<S> {

  S collectRows(@NotNull ResultSet rset)
    throws SQLException;

  boolean expectManyRows();

}

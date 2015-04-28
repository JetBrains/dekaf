package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.DBInterSeance;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcInterSeance implements DBInterSeance {

  @NotNull
  protected final JdbcInterSession session;


  protected JdbcInterSeance(@NotNull final JdbcInterSession session) {
    this.session = session;
  }

}

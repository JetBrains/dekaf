package org.jetbrains.jdba.core1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.DBQueryRunner;
import org.jetbrains.jdba.sql.SqlQuery;



/**
 * SQLQuery runner, serves one running of one query.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class BaseQueryRunner<S> implements DBQueryRunner {
  @NotNull
  protected final BaseSession session;

  @NotNull
  protected final SqlQuery<S> query;


  @Nullable
  protected Object[] params;


  BaseQueryRunner(@NotNull final BaseSession session, @NotNull final SqlQuery<S> query) {
    this.session = session;
    this.query = query;
  }


  @NotNull
  @Override
  public DBQueryRunner<S> withParams(final Object... params) {
    this.params = params;
    return this;
  }

  @NotNull
  @Override
  public DBQueryRunner packBy(final int rowsPerPack) {
    throw new RuntimeException("Method BaseQueryRunner.packBy() is not implemented yet.");
  }


  @Override
  public S run() {
    return session.processQuery(query.getSourceText(), params, query.getCollector());
  }

  @Override
  public Object nextPack() {
    throw new RuntimeException("Method BaseQueryRunner.nextPack() is not implemented yet.");
  }
}

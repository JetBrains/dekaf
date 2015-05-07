package org.jetbrains.jdba.core1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.sql.SQLQuery;



/**
 * SQLQuery runner, serves one running of one query.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class BaseQueryRunner<S> implements DBQueryRunner {
  @NotNull
  protected final BaseSession session;

  @NotNull
  protected final SQLQuery<S> query;


  @Nullable
  protected Object[] params;


  BaseQueryRunner(@NotNull final BaseSession session, @NotNull final SQLQuery<S> query) {
    this.session = session;
    this.query = query;
  }


  @Override
  public DBQueryRunner<S> withParams(final Object... params) {
    this.params = params;
    return this;
  }


  @Override
  public S run() {
    return session.processQuery(query.getSourceText(), params, query.getCollector());
  }
}

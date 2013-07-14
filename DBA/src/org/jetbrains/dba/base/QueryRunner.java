package org.jetbrains.dba.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * Query runner, serves one running of one query.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class QueryRunner<S> {
  @NotNull
  protected final DatabaseAbstractSession session;

  @NotNull
  protected final Query<S> query;


  @Nullable
  protected Object[] params;


  QueryRunner(@NotNull final DatabaseAbstractSession session, @NotNull final Query<S> query) {
    this.session = session;
    this.query = query;
  }


  public QueryRunner<S> withParams(final Object... params) {
    this.params = params;
    return this;
  }


  public S run() {
    return session.processQuery(query.sourceText, params, query.collector);
  }
}

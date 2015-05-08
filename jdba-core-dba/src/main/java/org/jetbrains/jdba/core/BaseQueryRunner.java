package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.intermediate.IntegralIntermediateCursor;
import org.jetbrains.jdba.intermediate.IntegralIntermediateSeance;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseQueryRunner<S> implements DBQueryRunner<S> {

  @NotNull
  private final IntegralIntermediateSeance myInterSeance;

  @NotNull
  private final ResultLayout<S> myResultLayout;


  protected BaseQueryRunner(@NotNull final IntegralIntermediateSeance interSeance,
                            @NotNull final ResultLayout<S> resultLayout) {
    myInterSeance = interSeance;
    myResultLayout = resultLayout;
  }

  @Override
  public BaseQueryRunner<S> withParams(final Object... params) {
    myInterSeance.setInParameters(params);
    return this;
  }

  @Override
  public S run() {
    myInterSeance.execute();
    final IntegralIntermediateCursor<S> cursor = myInterSeance.openCursor(0, myResultLayout);
    try {
      return cursor.fetch();
    }
    finally {
      cursor.close();
    }
  }

}

package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.intermediate.DBIntermediateConsts;
import org.jetbrains.jdba.intermediate.IntegralIntermediateCursor;
import org.jetbrains.jdba.intermediate.IntegralIntermediateSeance;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseQueryRunner<S> implements DBQueryRunner<S>, BaseSeanceRunner {

  @NotNull
  protected final IntegralIntermediateSeance myInterSeance;

  @NotNull
  private final ResultLayout<S> myResultLayout;

  @Nullable
  private IntegralIntermediateCursor<S> myCursor;

  private int myPackLimit = DBIntermediateConsts.DEFAULT_FETCH_LIMIT;

  private boolean myMultiPackMode = false;


  protected BaseQueryRunner(@NotNull final IntegralIntermediateSeance interSeance,
                            @NotNull final ResultLayout<S> resultLayout) {
    myInterSeance = interSeance;
    myResultLayout = resultLayout;
  }

  @NotNull
  @Override
  public synchronized BaseQueryRunner<S> withParams(final Object... params) {
    if (myCursor != null)
      throw new IllegalStateException("Cursor is open. Close (or completely fetch) it before run the query again.");

    myInterSeance.setInParameters(params);
    return this;
  }

  @NotNull
  @Override
  public synchronized BaseQueryRunner<S> packBy(final int rowsPerPack) {
    endFetching();

    myPackLimit = rowsPerPack;
    myMultiPackMode = true;
    return this;
  }

  @Override
  public synchronized S run() {
    endFetching();

    myInterSeance.execute();

    myCursor = myInterSeance.openCursor(0, myResultLayout);

    return fetchPack();
  }

  @Override
  public synchronized S nextPack() {
    if (myCursor != null) {
      return fetchPack();
    }
    else {
      return null;
    }
  }

  private S fetchPack() {
    assert myCursor != null;
    boolean ok = false;
    try {
      myCursor.setFetchLimit(myPackLimit);
      S result = myCursor.fetch();
      ok = true;
      return result;
    }
    finally {
      if (!ok || !myMultiPackMode || !myCursor.hasRows()) {
        myCursor.close();
        myCursor = null;
      }
    }
  }


  public synchronized void endFetching() {
    if (myCursor != null) {
      myCursor.close();
      myCursor = null;
    }
  }


  public synchronized void close() {
    endFetching();
    myInterSeance.close();
  }

}

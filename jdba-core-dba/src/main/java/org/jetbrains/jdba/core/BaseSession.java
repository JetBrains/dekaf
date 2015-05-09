package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.intermediate.IntegralIntermediateSeance;
import org.jetbrains.jdba.intermediate.IntegralIntermediateSession;
import org.jetbrains.jdba.sql.SqlCommand;
import org.jetbrains.jdba.sql.SqlQuery;
import org.jetbrains.jdba.sql.SqlScript;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseSession implements DBSession, DBTransaction  {

  @NotNull
  private final IntegralIntermediateSession myInterSession;

  protected BaseSession(@NotNull final IntegralIntermediateSession interSession) {
    myInterSession = interSession;
  }


  @Override
  public <R> R inTransaction(final InTransaction<R> operation) {
    final R result;
    boolean ok = false;
    myInterSession.beginTransaction();
    try {
      result = operation.run(this);
      myInterSession.commit();
      ok = true;
    }
    finally {
      if (!ok) {
        myInterSession.rollback();
      }
    }
    return result;
  }

  @Override
  public void inTransaction(final InTransactionNoResult operation) {
    boolean ok = false;
    myInterSession.beginTransaction();
    try {
      operation.run(this);
      myInterSession.commit();
      ok = true;
    }
    finally {
      if (!ok) {
        myInterSession.rollback();
      }
    }
  }

  @NotNull
  @Override
  public DBCommandRunner command(@NotNull final SqlCommand command) {
    final IntegralIntermediateSeance interSeance =
            myInterSession.openSeance(command.getSourceText(), null);
    return new BaseCommandRunner(interSeance);
  }

  @NotNull
  @Override
  public DBCommandRunner command(@NotNull final String commandText) {
    final IntegralIntermediateSeance interSeance =
            myInterSession.openSeance(commandText, null);
    return new BaseCommandRunner(interSeance);
  }

  @NotNull
  @Override
  public <S> BaseQueryRunner<S> query(@NotNull final SqlQuery<S> query) {
    final IntegralIntermediateSeance interSeance =
            myInterSession.openSeance(query.getSourceText(), null);
    return new BaseQueryRunner<S>(interSeance, query.getLayout());
  }

  @NotNull
  @Override
  public <S> BaseQueryRunner<S> query(@NotNull final String queryText,
                                      @NotNull final ResultLayout<S> layout) {
    SqlQuery<S> query = new SqlQuery<S>(queryText, layout);
    return this.query(query);
  }

  @NotNull
  @Override
  public DBScriptRunner script(@NotNull final SqlScript script) {
    // TODO implement BaseSession.script
    throw new RuntimeException("The BaseSession.script has not been implemented yet.");
  }


  public <I> I getSpecificService(@NotNull final Class<I> serviceInterface,
                                  @NotNull final String name) {
    return myInterSession.getSpecificService(serviceInterface, name);
  }

}

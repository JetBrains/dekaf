package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.intermediate.IntegralIntermediateSeance;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseCommandRunner implements DBCommandRunner, BaseSeanceRunner {

  private final IntegralIntermediateSeance myInterSeance;

  protected BaseCommandRunner(final IntegralIntermediateSeance interSeance) {
    myInterSeance = interSeance;
  }

  @Override
  public DBCommandRunner withParams(final Object... params) {
    myInterSeance.setInParameters(params);
    return this;
  }

  @Override
  public DBCommandRunner run() {
    myInterSeance.execute();
    return this;
  }


  @Override
  public void close() {
    myInterSeance.close();
  }
}

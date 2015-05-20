package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public abstract class BaseTestHelper<F extends DBFacade> implements DBTestHelper {

  @NotNull
  protected final F facade;


  protected BaseTestHelper(@NotNull final F facade) {
    this.facade = facade;
  }


}

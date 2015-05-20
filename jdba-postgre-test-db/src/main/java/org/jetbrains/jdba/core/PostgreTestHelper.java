package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class PostgreTestHelper extends BaseTestHelper<DBFacade> {

  public PostgreTestHelper(@NotNull final DBFacade facade) {
    super(facade);
  }

}

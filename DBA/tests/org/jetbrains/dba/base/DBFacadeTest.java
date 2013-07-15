package org.jetbrains.dba.base;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.jetbrains.dba.base.TestDB.*;
import static org.testng.Assert.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBFacadeTest extends DBTestCase {

  @Test
  public void connect1() {
    ourFacade.connect();
    assertTrue(ourFacade.isConnected());
  }

  @Test(dependsOnMethods = "connect1")
  public void runTrivialQuery() {
    final String simpleQuery;
    switch (ourRdbms) {
      case ORACLE: simpleQuery = "select 1 from dual"; break;
      default: simpleQuery = "select 1";
    }

    ourFacade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull DBTransaction tran) {

        tran.command(simpleQuery).run();

      }
    });
  }

}

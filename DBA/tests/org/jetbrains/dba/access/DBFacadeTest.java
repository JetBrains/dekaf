package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;
import org.testng.annotations.Test;

import static org.testng.Assert.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBFacadeTest extends DBTestCase {

  @Test
  public void connect1() {
    TestDB.ourFacade.connect();
    assertTrue(TestDB.ourFacade.isConnected());
  }

  @Test(dependsOnMethods = "connect1")
  public void runTrivialQuery() {
    final String simpleQuery;
    switch (TestDB.ourRdbms) {
      case ORACLE: simpleQuery = "select 1 from dual"; break;
      default: simpleQuery = "select 1";
    }

    TestDB.ourFacade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull DBTransaction tran) {

        tran.command(simpleQuery).run();

      }
    });
  }

}

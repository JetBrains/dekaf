package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.DBTestCase;
import org.jetbrains.dba.sql.SQLQuery;
import org.testng.annotations.Test;

import static org.jetbrains.dba.access.RowsCollectors.oneRow;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Test(groups = "all-db")
public class BaseFacadeTest extends DBTestCase {

  @Test
  public void connect1() {
    myFacade.connect();
    assertTrue(myFacade.isConnected());
  }

  @Test(dependsOnMethods = "connect1")
  public void runTrivialQuery() {
    final String simpleQuery;
    switch (myRdbms) {
      case ORACLE: simpleQuery = "select 44 from dual"; break;
      default: simpleQuery = "select 44";
    }

    final SQLQuery<Integer> query = new SQLQuery<Integer>(simpleQuery, oneRow(Integer.class));

    myFacade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull DBTransaction tran) {

        final Integer result = tran.query(query).run();

        assertEquals(result.intValue(), 44);

      }
    });
  }

  @Test(dependsOnMethods = "connect1")
  public void createTable() {
    myFacade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {

        session.command("create table T1 (F1 char(1))").run();
        session.command("drop table T1").run();

      }
    });
  }


}

package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.DBTestCase;
import org.jetbrains.dba.sql.SQLQuery;
import org.testng.annotations.Test;

import static org.jetbrains.dba.TestDB.*;
import static org.jetbrains.dba.access.RowsCollectors.oneRow;
import static org.testng.Assert.assertNotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Test(groups = "oracle")
public class OraDateAndTimeTest extends DBTestCase {


  @Test
  public void passJavaDate_toOracleDate() {
    createPassDateProc();
    passDateTo("pass_date");
  }

  @Test
  public void passJavaDate_toOracleTimestamp() {
    createPassTimestampProc();
    passDateTo("pass_timestamp");
  }

  @Test
  public void passTimestamp_toOracleDate() {
    createPassDateProc();
    passTimestampTo("pass_date");
  }

  @Test
  public void passTimestamp_toOracleTimestamp() {
    createPassTimestampProc();
    passTimestampTo("pass_timestamp");
  }


  private void createPassDateProc() {
    final String procPassDate =
      "create or replace procedure pass_date (p in date) as \n" +
      "begin                                                \n" +
      "    null;                                            \n" +
      "end;                                                 \n";
    performCommand(procPassDate);
  }

  private void createPassTimestampProc() {
    final String procPassDate =
      "create or replace procedure pass_timestamp (p in timestamp) as \n" +
      "begin                                                          \n" +
      "    null;                                                      \n" +
      "end;                                                           \n";
    performCommand(procPassDate);
  }


  private void passDateTo(final String procName) {
    final java.util.Date date = new java.util.Date();

    final String callPassDate = "begin " + procName + "(?); end;";
    ourDB.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull DBTransaction tran) {

        tran.command(callPassDate).withParams(date).run();
      }
    });
  }

  private void passTimestampTo(final String procName) {
    final java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());

    final String callPassDate = "begin " + procName + "(?); end;";
    ourDB.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull DBTransaction tran) {

        tran.command(callPassDate).withParams(timestamp).run();
      }
    });
  }


  @Test
  public void getJavaDate_fromOracleDate() {
    final SQLQuery<java.util.Date> query = ourSQL.query("select sysdate from dual", oneRow(java.util.Date.class));
    java.util.Date date = querySingleValue(query);
    assertNotNull(date);
  }

  @Test
  public void getJavaDate_fromOracleTimestamp() {
    final SQLQuery<java.util.Date> query = ourSQL.query("select cast(sysdate as timestamp) from dual", oneRow(java.util.Date.class));
    java.util.Date date = querySingleValue(query);
    assertNotNull(date);
  }

  @Test
  public void getSqlDate_fromOracleDate() {
    final SQLQuery<java.sql.Date> query = ourSQL.query("select sysdate from dual", oneRow(java.sql.Date.class));
    java.sql.Date date = querySingleValue(query);
    assertNotNull(date);
  }

  @Test
  public void getSqlDate_fromOracleTimestamp() {
    final SQLQuery<java.sql.Date> query = ourSQL.query("select cast(sysdate as timestamp) from dual", oneRow(java.sql.Date.class));
    java.sql.Date date = querySingleValue(query);
    assertNotNull(date);
  }

  @Test
  public void getSqlTimestamp_fromOracleDate() {
    final SQLQuery<java.sql.Timestamp> query = ourSQL.query("select sysdate from dual", oneRow(java.sql.Timestamp.class));
    java.sql.Timestamp timestamp = querySingleValue(query);
    assertNotNull(timestamp);
  }

  @Test
  public void getSqlTimestamp_fromOracleTimestamp() {
    final SQLQuery<java.sql.Timestamp> query = ourSQL.query("select cast(sysdate as timestamp) from dual", oneRow(java.sql.Timestamp.class));
    java.sql.Timestamp timestamp = querySingleValue(query);
    assertNotNull(timestamp);
  }


  private static <T> T querySingleValue(final SQLQuery<T> query) {
    return ourDB.inTransaction(new InTransaction<T>() {
      @Override
      public T run(@NotNull DBTransaction tran) {
        return tran.query(query).run();
      }
    });
  }
}

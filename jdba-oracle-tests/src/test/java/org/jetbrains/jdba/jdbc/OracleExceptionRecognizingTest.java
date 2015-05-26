package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.CommonExceptionRecognizingTest;
import org.jetbrains.jdba.core.DBTransaction;
import org.jetbrains.jdba.core.InTransactionNoResult;
import org.jetbrains.jdba.exceptions.NoRowsException;
import org.jetbrains.jdba.sql.Scriptum;
import org.jetbrains.jdba.sql.SqlCommand;
import org.junit.Test;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class OracleExceptionRecognizingTest extends CommonExceptionRecognizingTest {

  private final Scriptum myScriptum = Scriptum.of(getClass());

  @Test(expected = NoRowsException.class)
  public void recognize_NoRows() {
    final SqlCommand command = myScriptum.command("NoRows");
    DB.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {
        tran.command(command).run();
      }
    });
  }


}

package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.CommonExceptionRecognizingTest;
import org.jetbrains.dekaf.core.DBTransaction;
import org.jetbrains.dekaf.core.InTransactionNoResult;
import org.jetbrains.dekaf.exceptions.NoRowsException;
import org.jetbrains.dekaf.sql.Scriptum;
import org.jetbrains.dekaf.sql.SqlCommand;
import org.junit.Test;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class H2dbExceptionRecognizingTest extends CommonExceptionRecognizingTest {

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

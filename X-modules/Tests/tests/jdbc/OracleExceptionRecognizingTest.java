package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.core.CommonExceptionRecognizingTest;
import org.jetbrains.dekaf.exceptions.NoRowsException;
import org.jetbrains.dekaf.sql.Scriptum;
import org.jetbrains.dekaf.sql.SqlCommand;
import org.junit.Test;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class OracleExceptionRecognizingTest extends CommonExceptionRecognizingTest {

  private final Scriptum myScriptum = Scriptum.of(getClass());

  @Test(expected = NoRowsException.class)
  public void recognize_NoRows() {
    final SqlCommand command = myScriptum.command("NoRows");
    DB.inTransactionDo(tran -> tran.command(command).run());
  }


}

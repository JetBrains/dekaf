package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.CommonIntegrationCase;
import org.jetbrains.dekaf.exceptions.NoTableOrViewException;
import org.junit.Before;
import org.junit.Test;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class CommonExceptionRecognizingTest extends CommonIntegrationCase {

  @Before
  public void setUp() throws Exception {
    DB.connect();
  }

  @Test(expected = NoTableOrViewException.class)
  public void recognize_NoTableOrView() {
    DB.inTransactionDo(tran ->
        tran.query("select * from unexistent_table", Layouts.existence()).run()
    );
  }

}

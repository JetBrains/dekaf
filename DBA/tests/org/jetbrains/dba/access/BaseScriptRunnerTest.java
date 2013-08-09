package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.sql.SQL;
import org.jetbrains.dba.sql.SQLScript;
import org.jetbrains.dba.sql.SQLTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseScriptRunnerTest extends DBTestCase {

  private static SQL ourCommonSQL;


  @BeforeClass
  public static void setUp2() throws Exception {
    TestDB.ourFacade.connect();
    ourCommonSQL = new SQL();
    ourCommonSQL.assignResources(SQLTest.class.getClassLoader(), "sql/common");
  }


  @Test
  public void simple_script() {
    final SQLScript script = ourCommonSQL.script("##simple-script");

    TestDB.ourFacade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {

        session.script(script).run();

      }
    });

  }


}

package org.jetbrains.dba.sql;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 * @author Leonid Bushuev from JetBrains
 */
public class OraSQLTest {



  @DataProvider
  public String[][] pl_scenarios() {
    return new String[][] {
      { "ora-pl-scenario-begin" },
      { "ora-pl-scenario-declare" },
      { "ora-pl-scenario-create-procedure" },
    };
  }

  @Test(dataProvider = "pl_scenarios")
  public void pl_scenario(String name) {
    SQL sql = new OraSQL();
    sql.assignResources(SQLTest.class.getClassLoader(), "sql/oracle");

    SQLScript script = sql.script("##"+name);

    assertEquals(script.getCommands().size(), 1);
  }


  @DataProvider
  public String[][] sql_scripts_for_splitting() {
    return new String[][] {
      { "ora-sql-script-slash" },
      { "ora-sql-script-semicolon-1" },
      { "ora-sql-script-semicolon-2" },
    };
  }

  @Test(dataProvider = "sql_scripts_for_splitting")
  public void sql_script_splitting(String name) {
    SQL sql = new OraSQL();
    sql.assignResources(SQLTest.class.getClassLoader(), "sql/oracle");

    SQLScript script = sql.script("##"+name);

    assertEquals(script.getCommands().size(), 4);

    for (SQLCommand command : script.getCommands()) {
      assertNotNull(command);
      assertTrue(command.getSourceText().length() > 0);
    }
  }

  @Test(dataProvider = "sql_scripts_for_splitting")
  public void sql_script_truncating(String name) {
    SQL sql = new OraSQL();
    sql.assignResources(SQLTest.class.getClassLoader(), "sql/oracle");

    SQLScript script = sql.script("##"+name);

    for (SQLCommand command : script.getCommands()) {
      final String text = command.getSourceText();
      assertFalse(Character.isSpaceChar(text.charAt(0)), "Heading spaces must be truncated");
      assertFalse(Character.isSpaceChar(text.charAt(text.length()-1)), "Tailing spaces must be truncated");
    }
  }

  @Test
  public void mixed_script() {
    SQL sql = new OraSQL();
    sql.assignResources(SQLTest.class.getClassLoader(), "sql/oracle");

    SQLScript script = sql.script("##ora-mixed-script");

    assertEquals(script.getCommands().size(), 7);
  }

}

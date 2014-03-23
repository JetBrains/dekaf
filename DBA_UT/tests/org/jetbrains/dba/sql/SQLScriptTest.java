package org.jetbrains.dba.sql;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class SQLScriptTest {

  private final SQL sql = new SQL();

  @Test
  public void construct_2() {
    SQLScript script1 = sql.script("command1"),
              script2 = sql.script("command2");
    SQLScript script = new SQLScript(script1, script2);
    assertEquals(script.getCommands().size(), 2);
    assertEquals(script.getCommands().get(0).getSourceText(), "command1");
    assertEquals(script.getCommands().get(1).getSourceText(), "command2");
  }

  @Test
  public void toString_contains_all_commands() {
    SQLScript script = sql.script("command 1\ncommand 2");
    final String text = script.toString();
    assertTrue(text.contains("command 1"));
    assertTrue(text.contains("command 2"));
  }

}

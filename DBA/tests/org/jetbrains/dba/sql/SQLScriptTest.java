package org.jetbrains.dba.sql;

import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class SQLScriptTest {

  private final SQL sql = new SQL();

  @Test
  public void construct_3() {
    SQLScript script12 = sql.script("command1", "command2"),
              script34 = sql.script("command3", "command4");
    SQLScript script = new SQLScript(script12, script34);
    assertEquals(script.getCommands().size(), 4);
    assertEquals(script.getCommands().get(0).getSourceText(), "command1");
    assertEquals(script.getCommands().get(1).getSourceText(), "command2");
    assertEquals(script.getCommands().get(2).getSourceText(), "command3");
    assertEquals(script.getCommands().get(3).getSourceText(), "command4");
  }

  @Test
  public void toString_contains_all_commands() {
    SQLScript script = sql.script("command 1", "command 2");
    final String text = script.toString();
    assertTrue(text.contains("command 1"));
    assertTrue(text.contains("command 2"));
  }

}

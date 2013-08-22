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
  public void construct_1() {
    SQLCommand cmd1 = sql.command("command1");
    SQLCommand cmd2 = sql.command("command2");
    SQLScript script = new SQLScript(Arrays.asList(cmd1, cmd2));
    assertEquals(script.getCommands().size(), 2);
    assertSame(script.getCommands().get(0), cmd1);
    assertSame(script.getCommands().get(1), cmd2);
  }

  @Test
  public void construct_2() {
    SQLCommand cmd1 = sql.command("command1");
    SQLCommand cmd2 = sql.command("command2");
    SQLScript script = new SQLScript(cmd1, cmd2);
    assertEquals(script.getCommands().size(), 2);
    assertSame(script.getCommands().get(0), cmd1);
    assertSame(script.getCommands().get(1), cmd2);
  }

  @Test
  public void construct_3() {
    SQLCommand cmd1 = sql.command("command1"),
               cmd2 = sql.command("command2"),
               cmd3 = sql.command("command3"),
               cmd4 = sql.command("command4");
    SQLScript script12 = new SQLScript(cmd1, cmd2),
              script34 = new SQLScript(cmd3, cmd4);
    SQLScript script = new SQLScript(script12, script34);
    assertEquals(script.getCommands().size(), 4);
    assertSame(script.getCommands().get(0), cmd1);
    assertSame(script.getCommands().get(1), cmd2);
    assertSame(script.getCommands().get(2), cmd3);
    assertSame(script.getCommands().get(3), cmd4);
  }

  @Test
  public void toString_contains_all_commands() {
    SQLScript script = sql.script("command 1", "command 2");
    final String text = script.toString();
    assertTrue(text.contains("command 1"));
    assertTrue(text.contains("command 2"));
  }

}

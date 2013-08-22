package org.jetbrains.dba.sql;

import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;



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

}

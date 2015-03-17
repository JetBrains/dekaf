package org.jetbrains.jdba.sql;

import org.jetbrains.jdba.junitft.FineRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
@RunWith(FineRunner.class)
public class SQLScriptTest {

  private final SQL sql = new SQL();

  @Test
  public void construct_2() {
    SQLScript script1 = sql.script("command1"),
              script2 = sql.script("command2");
    SQLScript script = new SQLScript(script1, script2);
    assertThat((Integer)script.getCommands().size()).isEqualTo((Integer)2);
    assertThat(script.getCommands().get(0).getSourceText()).isEqualTo("command1");
    assertThat(script.getCommands().get(1).getSourceText()).isEqualTo("command2");
  }

  @Test
  public void toString_contains_all_commands() {
    SQLScript script = sql.script("command 1\ncommand 2");
    final String text = script.toString();
    assertThat(text).contains("command 1");
    assertThat(text).contains("command 2");
  }

}

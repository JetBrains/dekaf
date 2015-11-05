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
public class SqlScriptTest {


  @Test
  public void construct_2() {
    SqlScript script1 = new SqlScript("command1"),
              script2 = new SqlScript("command2");
    SqlScript script = new SqlScript(script1, script2);
    assertThat((Integer)script.getStatements().size()).isEqualTo((Integer)2);
    assertThat(script.getStatements().get(0).getSourceText()).isEqualTo("command1");
    assertThat(script.getStatements().get(1).getSourceText()).isEqualTo("command2");
  }

  @Test
  public void toString_contains_all_commands() {
    SqlScript script = new SqlScript("command 1", "command 2");
    final String text = script.toString();
    assertThat(text).contains("command 1")
                    .contains("command 2");
  }

}

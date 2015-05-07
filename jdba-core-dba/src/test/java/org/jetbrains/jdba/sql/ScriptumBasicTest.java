package org.jetbrains.jdba.sql;


import org.jetbrains.jdba.core.Layouts;
import org.jetbrains.jdba.junitft.FineRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
@RunWith(FineRunner.class)
public class ScriptumBasicTest {

  private static Scriptum myScriptum;


  @BeforeClass
  public static void setUp() {
    myScriptum = Scriptum.of(ScriptumBasicTest.class);
  }

  @Test
  public void theSimplestQuery() {
    SqlQuery<Character> query = myScriptum.query("TheSimplestQuery",
                                                 Layouts.singleOf(Character.class));
    assertThat(query.mySourceText).isEqualTo("select 1");
  }

  @Test
  public void theSimplestQuery_Oracle() {
    Scriptum scriptumForOracle =
            Scriptum.of(myScriptum, "Oracle");

    SqlQuery<Character> query = scriptumForOracle.query("TheSimplestQuery",
                                                        Layouts.singleOf(Character.class));
    assertThat(query.mySourceText).isEqualTo("select 1 from dual");
  }


  @Test
  public void basicCommand() {
    SqlCommand command = myScriptum.command("BasicCommand");
    assertThat(command.mySourceText)
            .startsWith("insert")
            .endsWith("values (1,2,3)");
  }



}

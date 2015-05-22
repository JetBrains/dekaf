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
  public void theCommand() {
    check_TheCommand("TheCommand");
  }

  @Test
  public void theCommand_semicolon_sameString() {
    check_TheCommand("TheCommand_semicolon_sameString");
  }

  @Test
  public void theCommand_semicolon_sameString_2() {
    check_TheCommand("TheCommand_semicolon_sameString_2");
  }

  @Test
  public void theCommand_semicolon_nextString() {
    check_TheCommand("TheCommand_semicolon_nextString");
  }

  @Test
  public void theCommand_semicolon_nextString_2() {
    check_TheCommand("TheCommand_semicolon_nextString_2");
  }

  @Test
  public void theCommand_slash_nextString() {
    check_TheCommand("TheCommand_slash_nextString");
  }

  @Test
  public void theCommand_slash_nextString_2() {
    check_TheCommand("TheCommand_slash_nextString_2");
  }

  protected void check_TheCommand(final String queryName) {
    SqlQuery<Character> query = myScriptum.query(queryName, Layouts.singleOf(Character.class));
    assertThat(query.mySourceText).isEqualTo("The Command");
  }

  @Test
  public void theCommand_Oracle() {
    Scriptum scriptumForOracle =
            Scriptum.of(myScriptum, "Oracle");

    SqlCommand command = scriptumForOracle.command("TheCommand");
    assertThat(command.mySourceText).isEqualTo("The Oracle Command");
  }


  @Test
  public void basicCommand() {
    SqlCommand command = myScriptum.command("BasicCommand");
    assertThat(command.mySourceText)
            .startsWith("insert")
            .endsWith("values (1,2,3)");
  }



}

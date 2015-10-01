package org.jetbrains.jdba.sql;


import org.jetbrains.jdba.core.Layouts;
import org.jetbrains.jdba.junitft.FineRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

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
            Scriptum.dialectOf(myScriptum, "Oracle");

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

  @Test
  public void basicCommand_descriptionContainsFileName() {
    SqlCommand command = myScriptum.command("BasicCommand");
    assertThat(command.getDescription()).contains(ScriptumBasicTest.class.getSimpleName());
    assertThat(command.toString()).contains(ScriptumBasicTest.class.getSimpleName());
  }

  @Test
  public void basicCommand_descriptionContainsFragmentName() {
    SqlCommand command = myScriptum.command("BasicCommand");
    assertThat(command.getDescription()).contains("BasicCommand");
    assertThat(command.toString()).contains("BasicCommand");
  }


  @Test
  public void plBlock1() {
    SqlCommand plb = myScriptum.command("PLBlock1");
    assertThat(plb.getSourceText()).contains("end;")
                                   .doesNotContain("/");
  }

  @Test
  public void postgresProcedure1() {
    SqlCommand plb = myScriptum.command("PostgresProcedure1");
    assertThat(plb.getSourceText()).startsWith("create")
                                   .endsWith("language plpgsql");
  }


  @Test
  public void name_basic() {
    SqlCommand command = myScriptum.command("TheCommand");
    assertThat(command.getName()).isEqualTo("TheCommand");
  }

  @Test
  public void name_adjustCase() {
    SqlCommand command = myScriptum.command("THECOMMAND");
    assertThat(command.getName()).isEqualTo("TheCommand");
  }


  @Test
  public void fileWithOneCommand_text() {
    Scriptum scriptum1 = Scriptum.of(ScriptumBasicTest.class, "FileWithOneCommand");
    TextFileFragment text = scriptum1.getText("TheCommand");
    assertThat(text).isNotNull();
    assertThat(text.text).contains("select something", "from some_table");
  }

  @Test
  public void fileWithOneCommand_script() {
    Scriptum scriptum1 = Scriptum.of(ScriptumBasicTest.class, "FileWithOneCommand");
    SqlScript script = scriptum1.script("TheCommand");
    assertThat(script).isNotNull();
    assertThat(script.count()).isEqualTo(1);

    List<? extends SqlStatement> statements = script.getStatements();
    SqlStatement statement = statements.get(0);
    assertThat(statement.getSourceText()).contains("select something", "from some_table");
  }

}

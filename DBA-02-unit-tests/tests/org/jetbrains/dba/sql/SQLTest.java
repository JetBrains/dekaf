package org.jetbrains.dba.sql;


import org.assertj.core.api.Assertions;
import org.jetbrains.dba.junit.FineRunner;
import org.jetbrains.dba.junit.TestWithParams;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.List;

import static org.jetbrains.dba.access.RowsCollectors.oneRow;
import static org.jetbrains.dba.utils.Strings.removeEnding;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
@RunWith(FineRunner.class)
public class SQLTest {


  private static final SQL sql = new SQL();


  @Test
  public void loadSourcesFromResources_ourTestSQLs() {
    sql.assignResources(SQLTest.class.getClassLoader(), "common");

    String text = sql.getSourceText("just-texts:Select2");
    Assertions.assertThat(text).isNotNull();
  }


  @Test
  public void extractNamedSubtext_noEndMarker1() {
    String text = "--=-- C1\n" +
                  "Create Table A\n" +
                  "--=-- C2\n" +
                  "Drop Table B";
    extractNamedSubtext_test(text);
  }

  @Test
  public void extractNamedSubtext_noEndMarker2() {
    String text = "--=-- C1\n" +
                  "Create Table A\n" +
                  "\n" +
                  "--=-- C2\n" +
                  "Drop Table B\n" +
                  "\n";
    extractNamedSubtext_test(text);
  }

  @Test
  public void extractNamedSubtext_OracleEndMarker1() {
    String text = "--=-- C1\n" +
                  "Create Table A\n" +
                  "/\n" +
                  "--=-- C2\n" +
                  "Drop Table B\n" +
                  "/";
    extractNamedSubtext_test(text);
  }

  @Test
  public void extractNamedSubtext_OracleEndMarker2() {
    String text = "--=-- C1\n" +
                  "Create Table A\n" +
                  "/\n" +
                  "\n" +
                  "--=-- C2\n" +
                  "Drop Table B\n" +
                  "/\n";
    extractNamedSubtext_test(text);
  }


  private static void extractNamedSubtext_test(String text) {
    String subText1 = SQL.extractNamedSubtext(text, "X", "C1");
    String subText2 = SQL.extractNamedSubtext(text, "X", "C2");
    Assertions.assertThat(subText1).isEqualTo("Create Table A");
    Assertions.assertThat(subText2).isEqualTo("Drop Table B");
  }


  static final String[][] JUST_TEXTS = {
      { "TinySelect", "select *" },
      { "Select2", "select *\nfrom My_Table" },
      { "SelectEnd", "select *\nfrom My_Table" },
  };

  @TestWithParams(params = "JUST_TEXTS")
  public void loadSourcesFromResources_queries(String name, String text) {
    String textName = "just-texts:" + name;
    String query = sql.getSourceText(textName);
    query = removeEnding(query, ";");
    Assertions.assertThat(query).isEqualTo(text);
  }


  @Test
  public void command_create() {
    SQLCommand command = sql.command("select * from dual");
    Assertions.assertThat(command).isNotNull();
    Assertions.assertThat(command.getSourceText()).isEqualTo("select * from dual");
  }

  @Test
  public void command_load() {
    final SQLCommand command = sql.command("##just-texts:TinySelect");
    Assertions.assertThat(command).isNotNull();
    Assertions.assertThat(command.getSourceText()).isEqualTo("select *");
  }


  @Test
  public void query_create() {
    SQLQuery<Byte> query = sql.query("select 44 from dual", oneRow(Byte.class));
    Assertions.assertThat(query).isNotNull();
    Assertions.assertThat(query.getSourceText()).isEqualTo("select 44 from dual");
  }

  @Test
  public void query_load() {
    final SQLQuery<Byte> query = sql.query("##just-texts:TinySelect", oneRow(Byte.class));
    Assertions.assertThat(query).isNotNull();
    Assertions.assertThat(query.getSourceText()).isEqualTo("select *");
  }


  @Test
  public void script_1() {
    final SQLScript script = sql.script("simple command");
    Assertions.assertThat((Integer)script.getCommands().size()).isEqualTo((Integer)1);
    Assertions.assertThat(script.getCommands().get(0).getSourceText()).isEqualTo("simple command");
  }

  @Test
  public void script_2_semicolon() {
    final SQLScript script = sql.script("command1;\n" +
                                        "command2");
    Assertions.assertThat((Integer)script.getCommands().size()).isEqualTo((Integer)2);
    Assertions.assertThat(script.getCommands().get(0).getSourceText()).isEqualTo("command1");
    Assertions.assertThat(script.getCommands().get(1).getSourceText()).isEqualTo("command2");
  }

  @Test
  public void script_2_semicolon_2() {
    final SQLScript script = sql.script("command1;\n" +
                                        "command2;");
    Assertions.assertThat((Integer)script.getCommands().size()).isEqualTo((Integer)2);
    Assertions.assertThat(script.getCommands().get(0).getSourceText()).isEqualTo("command1");
    Assertions.assertThat(script.getCommands().get(1).getSourceText()).isEqualTo("command2");
  }

  @Test
  public void script_2_semicolon_2n() {
    final SQLScript script = sql.script("command1;\n" +
                                        "command2;\n");
    Assertions.assertThat((Integer)script.getCommands().size()).isEqualTo((Integer)2);
    Assertions.assertThat(script.getCommands().get(0).getSourceText()).isEqualTo("command1");
    Assertions.assertThat(script.getCommands().get(1).getSourceText()).isEqualTo("command2");
  }

  @Test
  public void script_2_semicolon_onOwnLine() {
    String text =
      "command1  \n" +
      ";         \n" +
      "command2  \n";
    final SQLScript script = sql.script(text);
    Assertions.assertThat((Integer)script.getCommands().size()).isEqualTo((Integer)2);
    Assertions.assertThat(script.getCommands().get(0).getSourceText()).isEqualTo("command1");
    Assertions.assertThat(script.getCommands().get(1).getSourceText()).isEqualTo("command2");
  }


  @Test
  public void script_load() {
    final SQLScript script = sql.script("##simple-script");
    Assertions.assertThat(script).isNotNull();
    final List<SQLCommand> commands = script.getCommands();
    Assertions.assertThat((Integer)commands.size()).isEqualTo((Integer)5);
    Assertions.assertThat(commands.get(1).getSourceText()).isEqualTo("insert into Simple_Table values ('P1', 'Aaa')");
    Assertions.assertThat(commands.get(3).getSourceText()).isEqualTo("commit");
  }


  @Test
  public void script_empty() {
    final SQLScript script1 = sql.script("");
    Assertions.assertThat((Integer)script1.getCommands().size()).isEqualTo((Integer)0);

    final SQLScript script2 = sql.script("   \n    \n  ");
    Assertions.assertThat((Integer)script2.getCommands().size()).isEqualTo((Integer)0);
  }



}

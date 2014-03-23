package org.jetbrains.dba.sql;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.jetbrains.dba.access.RowsCollectors.oneRow;
import static org.jetbrains.dba.utils.Strings.removeEnding;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Test(sequential = true)
public class SQLTest {


  private static final SQL sql = new SQL();


  @Test
  public void loadSourcesFromResources_ourTestSQLs() {
    sql.assignResources(SQLTest.class.getClassLoader(), "sql/common");

    final String text = sql.getSourceText("just-texts:Select2");
    assertNotNull(text);
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
    assertEquals(subText1, "Create Table A");
    assertEquals(subText2, "Drop Table B");
  }


  @DataProvider
  String[][] justTexts() {
    return new String[][] {
      { "TinySelect", "select *" },
      { "Select2", "select *\nfrom My_Table" },
      { "SelectEnd", "select *\nfrom My_Table" },
    };
  }

  @Test(dataProvider = "justTexts", dependsOnMethods = "loadSourcesFromResources_ourTestSQLs")
  public void loadSourcesFromResources_queries(String name, String text) {
    String textName = "just-texts:" + name;
    String query = sql.getSourceText(textName);
    query = removeEnding(query, ";");
    assertEquals(query, text);
  }


  @Test
  public void command_create() {
    SQLCommand command = sql.command("select * from dual");
    assertNotNull(command);
    assertEquals(command.getSourceText(), "select * from dual");
  }

  @Test(dependsOnMethods = {"command_create", "loadSourcesFromResources_queries"})
  public void command_load() {
    final SQLCommand command = sql.command("##just-texts:TinySelect");
    assertNotNull(command);
    assertEquals(command.getSourceText(), "select *");
  }


  @Test
  public void query_create() {
    SQLQuery<Byte> query = sql.query("select 44 from dual", oneRow(Byte.class));
    assertNotNull(query);
    assertEquals(query.getSourceText(), "select 44 from dual");
  }

  @Test(dependsOnMethods = {"query_create", "loadSourcesFromResources_queries"})
  public void query_load() {
    final SQLQuery<Byte> query = sql.query("##just-texts:TinySelect", oneRow(Byte.class));
    assertNotNull(query);
    assertEquals(query.getSourceText(), "select *");
  }


  @Test(dependsOnMethods = "command_create")
  public void script_1() {
    final SQLScript script = sql.script("simple command");
    assertEquals(script.getCommands().size(), 1);
    assertEquals(script.getCommands().get(0).getSourceText(), "simple command");
  }

  @Test(dependsOnMethods = "script_1")
  public void script_2_semicolon() {
    final SQLScript script = sql.script("command1;\n" +
                                        "command2");
    assertEquals(script.getCommands().size(), 2);
    assertEquals(script.getCommands().get(0).getSourceText(), "command1");
    assertEquals(script.getCommands().get(1).getSourceText(), "command2");
  }

  @Test(dependsOnMethods = "script_1")
  public void script_2_semicolon_2() {
    final SQLScript script = sql.script("command1;\n" +
                                        "command2;");
    assertEquals(script.getCommands().size(), 2);
    assertEquals(script.getCommands().get(0).getSourceText(), "command1");
    assertEquals(script.getCommands().get(1).getSourceText(), "command2");
  }

  @Test(dependsOnMethods = "script_1")
  public void script_2_semicolon_2n() {
    final SQLScript script = sql.script("command1;\n" +
                                        "command2;\n");
    assertEquals(script.getCommands().size(), 2);
    assertEquals(script.getCommands().get(0).getSourceText(), "command1");
    assertEquals(script.getCommands().get(1).getSourceText(), "command2");
  }

  @Test(dependsOnMethods = "script_1")
  public void script_2_semicolon_onOwnLine() {
    String text =
      "command1  \n" +
      ";         \n" +
      "command2  \n";
    final SQLScript script = sql.script(text);
    assertEquals(script.getCommands().size(), 2);
    assertEquals(script.getCommands().get(0).getSourceText(), "command1");
    assertEquals(script.getCommands().get(1).getSourceText(), "command2");
  }


  @Test(dependsOnMethods = "command_load")
  public void script_load() {
    final SQLScript script = sql.script("##simple-script");
    assertNotNull(script);
    final List<SQLCommand> commands = script.getCommands();
    assertEquals(commands.size(), 5);
    assertEquals(commands.get(1).getSourceText(), "insert into Simple_Table values ('P1', 'Aaa')");
    assertEquals(commands.get(3).getSourceText(), "commit");
  }


  @Test
  public void script_empty() {
    final SQLScript script1 = sql.script("");
    assertEquals(script1.getCommands().size(), 0);

    final SQLScript script2 = sql.script("   \n    \n  ");
    assertEquals(script2.getCommands().size(), 0);
  }



}

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


  private static SQL ourCommonSQL;


  @Test
  public void loadSourcesFromResources_ourTestSQLs() {
    ourCommonSQL = new SQL();
    ourCommonSQL.assignResources(SQLTest.class.getClassLoader(), "sql/common");

    final String text = ourCommonSQL.getSourceText("just-texts:Select2");
    assertNotNull(text);
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
    String query = ourCommonSQL.getSourceText(textName);
    query = removeEnding(query, ";");
    assertEquals(query, text);
  }


  @Test
  public void command_create() {
    SQL sql = new SQL();
    SQLCommand command = sql.command("select * from dual");
    assertNotNull(command);
    assertEquals(command.getSourceText(), "select * from dual");
  }

  @Test(dependsOnMethods = {"command_create", "loadSourcesFromResources_queries"})
  public void command_load() {
    final SQLCommand command = ourCommonSQL.command("##just-texts:TinySelect");
    assertNotNull(command);
    assertEquals(command.getSourceText(), "select *");
  }


  @Test
  public void query_create() {
    SQL sql = new SQL();
    SQLQuery<Byte> query = sql.query("select 44 from dual", oneRow(Byte.class));
    assertNotNull(query);
    assertEquals(query.getSourceText(), "select 44 from dual");
  }

  @Test(dependsOnMethods = {"query_create", "loadSourcesFromResources_queries"})
  public void query_load() {
    final SQLQuery<Byte> query = ourCommonSQL.query("##just-texts:TinySelect", oneRow(Byte.class));
    assertNotNull(query);
    assertEquals(query.getSourceText(), "select *");
  }


  @Test(dependsOnMethods = "command_load")
  public void script_load() {
    final SQLScript script = ourCommonSQL.script("##simple-script");
    assertNotNull(script);
    final List<SQLCommand> commands = script.getCommands();
    assertEquals(commands.size(), 5);
    assertEquals(commands.get(1).getSourceText(), "insert into Simple_Table values ('P1', 'Aaa')");
    assertEquals(commands.get(3).getSourceText(), "commit");
  }




}

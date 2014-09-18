package org.jetbrains.dba.sql;

import org.jetbrains.dba.junit.FineRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.jetbrains.dba.junit.Assertions.assertEquals;
import static org.jetbrains.dba.junit.Assertions.assertTrue;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(FineRunner.class)
public class SQLScriptBuilderTest {


  @Test
  public void parse_1() {
    String commandText = "select * from dual";
    final SQLScript script = build(commandText);

    assertEquals(script.myCount, 1);
    assertEquals(script.getCommands().get(0).getSourceText(), commandText);
  }


  /*
  @Test
  public void parse_2_in_one_line() {
    String text = "create table X; drop table X";
    final SQLScript script = build(text);

    assertEquals(script.myCount, 2);
    assertEquals(script.getCommands().get(0).getSourceText(), "create table X");
    assertEquals(script.getCommands().get(1).getSourceText(), "drop table X");
  }
  */


  @Test
  public void parse_2_in_2_lines() {
    String text = "create table X;\n drop table X";
    final SQLScript script = build(text);

    assertEquals(script.myCount, 2);
    assertEquals(script.getCommands().get(0).getSourceText(), "create table X");
    assertEquals(script.getCommands().get(1).getSourceText(), "drop table X");
  }


  @Test
  public void parse_singleLineComment() {
    String text = "-- a single line comment \n" +
                  "do something";
    final SQLScript script = build(text);

    assertEquals(script.myCount, 1);
    assertEquals(script.getCommands().get(0).getSourceText(), "do something");
  }


  @Test
  public void parse_singleLineComment_preserveOracleHint() {
    String text = "select --+index(i) \n" +
                  "      all fields   \n" +
                  "from my_table      \n";
    final SQLScript script = build(text);

    assertEquals(script.myCount, 1);
    final String queryText = script.getCommands().get(0).getSourceText();
    assertTrue(queryText.contains("--+index(i)"));
  }


  @Test
  public void parse_multiLineComment_1() {
    String text = "/* a multi-line comment*/\n" +
                  "do something";
    final SQLScript script = build(text);

    assertEquals(script.myCount, 1);
    assertEquals(script.getCommands().get(0).getSourceText(), "do something");
  }


  @Test
  public void parse_multiLineComment_3() {
    String text = "/*                      \n" +
                  " * a multi-line comment \n" +
                  " */                     \n" +
                  "do something            \n";
    final SQLScript script = build(text);

    assertEquals(script.myCount, 1);
    assertEquals(script.getCommands().get(0).getSourceText(), "do something");
  }


  @Test
  public void parse_multiLineComment_preserveOracleHint() {
    String text = "select /*+index(i)*/ * \n" +
                  "from table             \n";
    final SQLScript script = build(text);

    assertEquals(script.myCount, 1);
    final String queryText = script.getCommands().get(0).getSourceText();
    assertTrue(queryText.contains("/*+index(i)*/"));
  }


  private SQLScript build(String text) {
    SQLScriptBuilder b = new SQLScriptBuilder(new SQL());
    b.parse(text);
    return b.build();
  }


}

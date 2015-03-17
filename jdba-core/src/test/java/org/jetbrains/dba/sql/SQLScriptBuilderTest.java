package org.jetbrains.dba.sql;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import testing.junit.FineRunner;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
@RunWith(FineRunner.class)
public class SQLScriptBuilderTest {


  @Test
  public void parse_1() {
    String commandText = "select * from dual";
    final SQLScript script = build(commandText);

    assertThat((Integer)script.myCount).isEqualTo((Integer)1);
    assertThat(script.getCommands().get(0).getSourceText()).isEqualTo(commandText);
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

    assertThat((Integer)script.myCount).isEqualTo((Integer)2);
    assertThat(script.getCommands().get(0).getSourceText()).isEqualTo("create table X");
    assertThat(script.getCommands().get(1).getSourceText()).isEqualTo("drop table X");
  }


  @Test
  public void parse_singleLineComment() {
    String text = "-- a single line comment \n" +
                  "do something";
    final SQLScript script = build(text);

    assertThat((Integer)script.myCount).isEqualTo((Integer)1);
    assertThat(script.getCommands().get(0).getSourceText()).isEqualTo("do something");
  }


  @Test
  public void parse_singleLineComment_preserveOracleHint() {
    String text = "select --+index(i) \n" +
                  "      all fields   \n" +
                  "from my_table      \n";
    final SQLScript script = build(text);

    assertThat((Integer)script.myCount).isEqualTo((Integer)1);
    final String queryText = script.getCommands().get(0).getSourceText();
    assertThat(queryText).contains("--+index(i)");
  }


  @Test
  public void parse_multiLineComment_1() {
    String text = "/* a multi-line comment*/\n" +
                  "do something";
    final SQLScript script = build(text);

    assertThat((Integer)script.myCount).isEqualTo((Integer)1);
    assertThat(script.getCommands().get(0).getSourceText()).isEqualTo("do something");
  }


  @Test
  public void parse_multiLineComment_3() {
    String text = "/*                      \n" +
                  " * a multi-line comment \n" +
                  " */                     \n" +
                  "do something            \n";
    final SQLScript script = build(text);

    assertThat((Integer)script.myCount).isEqualTo((Integer)1);
    assertThat(script.getCommands().get(0).getSourceText()).isEqualTo("do something");
  }


  @Test
  public void parse_multiLineComment_preserveOracleHint() {
    String text = "select /*+index(i)*/ * \n" +
                  "from table             \n";
    final SQLScript script = build(text);

    assertThat((Integer)script.myCount).isEqualTo((Integer)1);
    final String queryText = script.getCommands().get(0).getSourceText();
    assertThat(queryText).contains("/*+index(i)*/");
  }


  private SQLScript build(String text) {
    SQLScriptBuilder b = new SQLScriptBuilder(new SQL());
    b.parse(text);
    return b.build();
  }


}

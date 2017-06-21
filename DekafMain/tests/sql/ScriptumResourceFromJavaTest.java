package org.jetbrains.dekaf.sql;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class ScriptumResourceFromJavaTest {


  private static ScriptumResourceFromJava myResource;

  @BeforeAll
  public static void setUpClass() {
    myResource =
          new ScriptumResourceFromJava(ScriptumResourceFromJavaTest.class.getClassLoader(),
                                       "org/jetbrains/dekaf/sql/ScriptumResourceFromJavaTest.sql");
  }


  @Test
  public void load() {
    int count = myResource.count();
    assertThat(count).isGreaterThan(1);
  }

  @Test
  public void first() {
    TextFragment first = myResource.get("FIRST");
    assertThat(first.text).isEqualTo("select something from anything");
  }

  @Test
  public void first_lower() {
    TextFragment first = myResource.get("first");
    assertThat(first.text).isEqualTo("select something from anything");
  }

  @Test
  public void second() {
    TextFragment second = myResource.get("SECOND");
    assertThat(second.text).isEqualTo("select 2");
  }

  @Test
  public void second_oracle() {
    TextFragment second = myResource.get("SECOND+ORACLE");
    assertThat(second.text).isEqualTo("select 2 from dual");
  }

  @Test
  public void multi_line() {
    String expectedText =
            "select columns\n" +
            "from table\n" +
            "where the condition is met";
    TextFragment text = myResource.get("MULTI_LINE");
    assertThat(text.text).isEqualTo(expectedText);
  }

  @Test
  public void lower_case() {
    TextFragment second = myResource.get("lower_case");
    assertThat(second.text).isEqualTo("select something_lower_case");
  }

  @Test
  public void lower_case_in_upper_case() {
    TextFragment second = myResource.get("LOWER_CASE");
    assertThat(second.text).isEqualTo("select something_lower_case");
  }

  @Test
  public void international_German() {
    TextFragment second = myResource.get("München_Straße");
    assertThat(second.text).contains("etwas_von_München");
  }

  @Test
  public void international_Russian() {
    TextFragment second = myResource.get("Санкт-Петербург");
    assertThat(second.text).contains("Невский Проспект");
  }

  @Test
  public void zero() {
    TextFragment second = myResource.get("0");
    assertThat(second.text).contains("Some comment at the top of the file");
  }

  @Test
  public void the_end() {
    TextFragment second = myResource.get("THE_END");
    assertThat(second.text).contains("select last_value");
  }


}
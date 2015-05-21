package org.jetbrains.jdba.sql;

import org.jetbrains.jdba.junitft.FineRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
@RunWith(FineRunner.class)
public class ScriptumResourceFromJavaTest {


  private static ScriptumResourceFromJava myResource;

  @BeforeClass
  public static void setUpClass() {
    myResource =
          new ScriptumResourceFromJava(ScriptumResourceFromJavaTest.class.getClassLoader(),
                                       "org/jetbrains/jdba/sql/ScriptumResourceFromJavaTest.sql");
  }


  @Test
  public void load() {
    int count = myResource.count();
    assertThat(count).isGreaterThan(1);
  }

  @Test
  public void first() {
    TextFragment first = myResource.find("FIRST");
    assertThat(first.text).isEqualTo("select something from anything");
  }

  @Test
  public void first_lower() {
    TextFragment first = myResource.find("first");
    assertThat(first.text).isEqualTo("select something from anything");
  }

  @Test
  public void second() {
    TextFragment second = myResource.find("SECOND");
    assertThat(second.text).isEqualTo("select 2");
  }

  @Test
  public void second_oracle() {
    TextFragment second = myResource.find("SECOND+ORACLE");
    assertThat(second.text).isEqualTo("select 2 from dual");
  }

  @Test
  public void multi_line() {
    String expectedText =
            "select columns\n" +
            "from table\n" +
            "where the condition is met";
    TextFragment text = myResource.find("MULTI_LINE");
    assertThat(text.text).isEqualTo(expectedText);
  }

  @Test
  public void lower_case() {
    TextFragment second = myResource.find("lower_case");
    assertThat(second.text).isEqualTo("select something_lower_case");
  }

  @Test
  public void lower_case_in_upper_case() {
    TextFragment second = myResource.find("LOWER_CASE");
    assertThat(second.text).isEqualTo("select something_lower_case");
  }

  @Test
  public void international_German() {
    TextFragment second = myResource.find("München_Straße");
    assertThat(second.text).contains("etwas_von_München");
  }

  @Test
  public void international_Russian() {
    TextFragment second = myResource.find("Санкт-Петербург");
    assertThat(second.text).contains("Невский Проспект");
  }

  @Test
  public void zero() {
    TextFragment second = myResource.find("0");
    assertThat(second.text).contains("Some comment at the top of the file");
  }

  @Test
  public void the_end() {
    TextFragment second = myResource.find("THE_END");
    assertThat(second.text).contains("select last_value");
  }


}
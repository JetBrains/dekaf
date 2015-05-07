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
    String first = myResource.find("FIRST");
    assertThat(first).isEqualTo("select something from anything");
  }

  @Test
  public void first_lower() {
    String first = myResource.find("first");
    assertThat(first).isEqualTo("select something from anything");
  }

  @Test
  public void second() {
    String second = myResource.find("SECOND");
    assertThat(second).isEqualTo("select 2");
  }

  @Test
  public void second_oracle() {
    String second = myResource.find("SECOND+ORACLE");
    assertThat(second).isEqualTo("select 2 from dual");
  }

  @Test
  public void multi_line() {
    String expectedText =
            "select columns\n" +
            "from table\n" +
            "where the condition is met";
    String text = myResource.find("MULTI_LINE");
    assertThat(text).isEqualTo(expectedText);
  }

  @Test
  public void lower_case() {
    String second = myResource.find("lower_case");
    assertThat(second).isEqualTo("select something_lower_case");
  }

  @Test
  public void lower_case_in_upper_case() {
    String second = myResource.find("LOWER_CASE");
    assertThat(second).isEqualTo("select something_lower_case");
  }

  @Test
  public void international_German() {
    String second = myResource.find("München_Straße");
    assertThat(second).contains("etwas_von_München");
  }

  @Test
  public void international_Russian() {
    String second = myResource.find("Санкт-Петербург");
    assertThat(second).contains("Невский Проспект");
  }

  @Test
  public void zero() {
    String second = myResource.find("0");
    assertThat(second).contains("Some comment at the top of the file");
  }

  @Test
  public void the_end() {
    String second = myResource.find("THE_END");
    assertThat(second).contains("select last_value");
  }


}
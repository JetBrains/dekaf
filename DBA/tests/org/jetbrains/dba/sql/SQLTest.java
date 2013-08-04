package org.jetbrains.dba.sql;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.jetbrains.dba.utils.Strings.*;
import static org.testng.Assert.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
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

}

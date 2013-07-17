package org.jetbrains.dba.sql;

import org.testng.annotations.Test;

import static org.testng.Assert.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class SQLTest {

  @Test
  public void loadSourcesFromResources_ourTestSQLs() {
    SQL sql = new SQL();
    sql.assignResources(SQLTest.class.getClassLoader(), "sql/oracle");

    final String text = sql.getSourceText("basic-test:CreateTable");
    assertNotNull(text);
  }

}

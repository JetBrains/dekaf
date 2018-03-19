package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.sql.SqlCommand;
import org.jetbrains.dekaf.sql.SqlQuery;
import org.jetbrains.dekaf.sql.SqlScript;
import org.junit.Test;




public class BaseScriptRunnerTest extends BaseInMemoryDBFacadeCase {

  @Test
  public void basic_2() {
    final SqlScript script = new SqlScript("create table TT2(X1 integer)",
                                           "drop table TT2");

    myFacade.inSessionDo(session -> {

        session.script(script).run();

    });
  }

  @Test
  public void basic_4() {
    final SqlScript script = new SqlScript("create table TT4(X1 integer)",
                                           "insert into TT4 values (44)",
                                           "select * from TT4",
                                           "drop table TT4");

    myFacade.inSessionDo(session -> {

        session.script(script).run();

    });
  }

  @Test
  public void basic_4_with_query() {
    final SqlScript script =
        new SqlScript(new SqlCommand("create table TT4a(X1 integer)"),
                      new SqlCommand("insert into TT4a values (44)"),
                      new SqlQuery<Boolean>("select * from TT4a", Layouts.existence()),
                      new SqlCommand("drop table TT4a"));

    myFacade.inSessionDo(session -> {

        session.script(script).run();

    });
  }


}
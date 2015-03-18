package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.SQL;
import org.jetbrains.jdba.sql.SQLCommand;
import org.jetbrains.jdba.sql.SQLQuery;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import testing.categories.ForEveryRdbms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.TestDB2.FACADE;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Category(ForEveryRdbms.class)
@FixMethodOrder(MethodSorters.JVM)
public class BaseCommandRunnerTest {


  private final SQL SQL = FACADE.sql();


  @Before
  public void setUp() throws Exception {
    FACADE.connect();
    //TestDB.zapSchema();
  }


  @Test
  public void run_create_and_drop_table() {
    final SQLCommand create = SQL.command("create table table_1 (dummy_char char(1))");
    final SQLCommand drop = SQL.command("drop table table_1");

    FACADE.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {

        session.command(create).run();

        session.command(drop).run();

      }
    });
  }


  @Test
  public void run_create_insert_select_drop_table() {
    final SQLCommand create = SQL.command("create table table_2 (abb char(2))");
    final SQLCommand insert = SQL.command("insert into table_2 (abb) values ('LB')");
    final SQLQuery<String> select = SQL.query("select abb from table_2", RowsCollectors.oneRow(String.class));
    final SQLCommand drop = SQL.command("drop table table_2");

    FACADE.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {

        session.command(create).run();

        session.command(insert).run();

        String abb = session.query(select).run();
        assertThat(abb).isEqualTo("LB");

        session.command(drop).run();

      }
    });
  }




}

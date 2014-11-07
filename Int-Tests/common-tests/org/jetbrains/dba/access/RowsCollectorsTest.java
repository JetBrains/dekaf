package org.jetbrains.dba.access;

import org.jetbrains.dba.sql.SQLQuery;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import testing.categories.ForEveryRdbms;

import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;
import static org.jetbrains.dba.TestDB.FACADE;
import static org.jetbrains.dba.TestDB.UTILS;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Category(ForEveryRdbms.class)
@FixMethodOrder(MethodSorters.JVM)
public class RowsCollectorsTest {

  private static class BasicBigRow {
    Character char_column;
    String string_column;
    Integer number_column;
  }

  private static class BasicSmallRow {
    Character char_column;
    String string_column;
    Integer number_column;
  }

  @Test
  public void oneRow_basic_big() {
    prepareOneRowTable();

    SQLQuery<BasicBigRow> query =
      FACADE.sql().query("select * from one_row_table", RowsCollectors.oneRow(BasicBigRow.class));

    BasicBigRow row = UTILS.query(query);

    then(row.char_column).isEqualTo(new Character('A'));
    then(row.string_column).isEqualTo("Hello");
    then(row.number_column).isEqualTo(new Integer(123456789));
  }

  @Test
  public void oneRow_basic_small() {
    prepareOneRowTable();

    SQLQuery<BasicSmallRow> query =
      FACADE.sql().query("select * from one_row_table", RowsCollectors.oneRow(BasicSmallRow.class));

    BasicSmallRow row = UTILS.query(query);

    then(row.char_column).isEqualTo('A');
    then(row.string_column).isEqualTo("Hello");
    then(row.number_column).isEqualTo(123456789);
  }

  @Test
  public void rowsExist_basic() {
    prepareOneRowTable();

    SQLQuery<Boolean> query =
      FACADE.sql().query("select 1 from one_row_table", RowsCollectors.rowsExist());

    // exactly one row
    boolean result1 = UTILS.query(query);
    then(result1).isTrue();

    // two rows
    UTILS.run("insert into one_row_table values ('B', 'Bye', -1)");
    boolean result2 = UTILS.query(query);
    then(result2).isTrue();

    // no rows
    UTILS.run("delete from one_row_table");
    boolean result0 = UTILS.query(query);
    then(result0).isFalse();
  }


  private void prepareOneRowTable() {
    String createTableCmd =
      "create table one_row_table ( \n" +
      "  char_column   char(1),     \n" +
      "  string_column varchar(26), \n" +
      "  number_column numeric(9)   \n" +
      ")                            \n";

    UTILS.ensureNoTablesLike("one_row_table");
    UTILS.run(createTableCmd);
    UTILS.run("insert into one_row_table values ('A', 'Hello', 123456789)");
  }


  @Test
  public void map_basic() {
    String createTableCmd =
      "create table map_table (               \n" +
      "  kkk numeric(9) not null primary key, \n" +
      "  vvv varchar(26) not null             \n" +
      ")                                      \n";

    UTILS.ensureNoTablesLike("map_table");
    UTILS.run(createTableCmd);
    UTILS.run("insert into map_table values (1, 'Einz')");
    UTILS.run("insert into map_table values (2, 'Zwei')");
    UTILS.run("insert into map_table values (3, 'Drei')");
    UTILS.run("insert into map_table values (4, 'Vier')");

    SQLQuery<Map<Integer,String>> query =
      FACADE.sql().query("select * from map_table", RowsCollectors.map(Integer.class, String.class));
    Map<Integer,String> map = UTILS.query(query);

    then(map).containsEntry(1, "Einz")
             .containsEntry(2, "Zwei")
             .containsEntry(3, "Drei")
             .containsEntry(4, "Vier");
  }
}

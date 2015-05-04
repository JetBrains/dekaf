package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.Layouts;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.core.Layouts.*;



public class JdbcInterCursorTest extends BaseHyperSonicCase {


  @Test
  public void query_single_Integer() {
    String queryText = "select 123 from information_schema.schemata limit 1";
    JdbcInterSeance seance = query(queryText);
    JdbcInterCursor<Integer> cursor = seance.openDefaultCursor(singleOf(Integer.class));
    assertThat(cursor.isOpened()).isTrue();
    Integer v = cursor.fetch();
    assertThat(v).isNotNull()
                 .isEqualTo(123);
    assertThat(cursor.isOpened()).isFalse();
  }


  @Test
  public void query_single_row_array_of_Integers() {
    String queryText = "select 123, 456, 789 from information_schema.schemata limit 1";
    JdbcInterSeance seance = query(queryText);
    JdbcInterCursor<Integer[]> cursor = seance.openDefaultCursor(rowOf(arrayOf(3, Integer.class)));
    assertThat(cursor.isOpened()).isTrue();
    Integer[] array = cursor.fetch();
    assertThat(array).isNotNull()
                     .containsExactly(123, 456, 789);
    assertThat(cursor.isOpened()).isFalse();
  }


  @Test
  public void query_one_column_of_Integers() {
    String queryText =
            "select min(123) from information_schema.schemata \n" +
            "union all                                        \n" +
            "select min(456) from information_schema.schemata \n" +
            "union all                                        \n" +
            "select min(789) from information_schema.schemata \n";
    JdbcInterSeance seance = query(queryText);
    JdbcInterCursor<Integer[]> cursor = seance.openDefaultCursor(columnOf(Integer.class));
    assertThat(cursor.isOpened()).isTrue();
    Integer[] array = cursor.fetch();
    assertThat(array).isNotNull()
                     .contains(123, 456, 789)
                     .hasSize(3);
    assertThat(cursor.isOpened()).isFalse();
  }


  @Test
  public void query_matrix_of_Integers() {
    String queryText =
            "select min(111), min(222), min(333) from information_schema.schemata \n" +
            "union all                                                            \n" +
            "select min(444), min(555), min(666) from information_schema.schemata \n" +
            "union all                                                            \n" +
            "select min(777), min(888), min(999) from information_schema.schemata \n";
    JdbcInterSeance seance = query(queryText);
    JdbcInterCursor<Integer[][]> cursor = seance.openDefaultCursor(arrayOf(arrayOf(3,
                                                                                   Integer.class)));
    assertThat(cursor.isOpened()).isTrue();
    Integer[][] matrix = cursor.fetch();
    assertThat(matrix).isNotNull()
                      .hasSize(3);
    assertThat(matrix[0]).isNotNull().hasSize(3);
    assertThat(matrix[1]).isNotNull().hasSize(3);
    assertThat(matrix[2]).isNotNull().hasSize(3);
  }


  @Test
  public void query_one_column_of_ints() {
    String queryText =
            "select min(123) from information_schema.schemata \n" +
            "union all                                        \n" +
            "select min(456) from information_schema.schemata \n" +
            "union all                                        \n" +
            "select min(789) from information_schema.schemata \n";
    JdbcInterSeance seance = query(queryText);
    JdbcInterCursor<int[]> cursor = seance.openDefaultCursor(columnOfInts(10));
    assertThat(cursor.isOpened()).isTrue();
    int[] array = cursor.fetch();
    assertThat(array).isNotNull()
                     .contains(123, 456, 789)
                     .hasSize(3);
    assertThat(cursor.isOpened()).isFalse();
  }


  @Test
  public void query_one_column_of_longs() {
    String queryText =
            "select min(123456789000) from information_schema.schemata \n" +
            "union all                                                 \n" +
            "select min(456789123000) from information_schema.schemata \n" +
            "union all                                                 \n" +
            "select min(789123456000) from information_schema.schemata \n";
    JdbcInterSeance seance = query(queryText);
    JdbcInterCursor<long[]> cursor = seance.openDefaultCursor(columnOfLongs(10));
    assertThat(cursor.isOpened()).isTrue();
    long[] array = cursor.fetch();
    assertThat(array).isNotNull()
                     .contains(123456789000L, 456789123000L, 789123456000L)
                     .hasSize(3);
    assertThat(cursor.isOpened()).isFalse();
  }


  @Test
  public void query_number_basic() {
    final String[] createTable = new String[] {
           "create table basic_numbers_table (B tinyint, S smallint, I integer, L bigint)",
           "insert into basic_numbers_table values (10, 1000, 1000000, 1000000000000)"
    };

    final String query = "select * from basic_numbers_table";

    JdbcInterSession session = openSession();
    performStatements(session, createTable);

    JdbcInterSeance seance = session.openSeance(query, null);
    seance.execute();
    JdbcInterCursor<Number[]> cursor =
            seance.openDefaultCursor(Layouts.rowOf(arrayOf(4, Number.class)));
    Number[] row = cursor.fetch();
    seance.close();

    assertThat(row).isNotNull()
                   .hasSize(4);
    assertThat(row[0]).isInstanceOf(Byte.class).isEqualTo((byte) 10);
    assertThat(row[1]).isInstanceOf(Short.class).isEqualTo((short) 1000);
    assertThat(row[2]).isInstanceOf(Integer.class).isEqualTo(1000000);
    assertThat(row[3]).isInstanceOf(Long.class).isEqualTo(1000000000000L);
  }


  @Test
  public void query_floats() {
    final String[] createTable = new String[] {
           "create table float_numbers_table (F float, D double, R real)",
           "insert into float_numbers_table values (3.1415, 2.718281828, 26.74)"
    };

    final String query = "select * from float_numbers_table";

    JdbcInterSession session = openSession();
    performStatements(session, createTable);

    JdbcInterSeance seance = session.openSeance(query, null);
    seance.execute();
    JdbcInterCursor<Object[]> cursor =
            seance.openDefaultCursor(Layouts.rowOf(arrayOf(Float.class, Double.class, Number.class)));
    Object[] row = cursor.fetch();
    seance.close();

    assertThat(row).isNotNull()
                   .hasSize(3);
    assertThat(row[0]).isInstanceOf(Float.class).isEqualTo(3.1415f);
    assertThat(row[1]).isInstanceOf(Double.class).isEqualTo(2.718281828d);
    assertThat(row[2]).isInstanceOfAny(Float.class, Double.class);
  }


  @Test
  public void query_decimals() {
    final String[] createTable = new String[] {
           "create table decimal_numbers_table (D1 decimal(24), D2 decimal(36,6))",
           "insert into decimal_numbers_table values (123456781234567812345678, 123456789012345678901234567890.666666)"
    };

    final String query = "select * from decimal_numbers_table";

    JdbcInterSession session = openSession();
    performStatements(session, createTable);

    JdbcInterSeance seance = session.openSeance(query, null);
    seance.execute();
    JdbcInterCursor<BigDecimal[]> cursor =
            seance.openDefaultCursor(Layouts.rowOf(arrayOf(2, BigDecimal.class)));
    BigDecimal[] row = cursor.fetch();
    seance.close();

    assertThat(row).isNotNull()
                   .hasSize(2);
    assertThat(row[0]).isInstanceOf(BigDecimal.class).isEqualTo(new BigDecimal("123456781234567812345678"));
    assertThat(row[1]).isInstanceOf(BigDecimal.class).isEqualTo(new BigDecimal("123456789012345678901234567890.666666"));
  }


  @Test
  public void query_hash_map() {
    JdbcInterSession session = openSession();
    performStatements(session,
                      "create table if not exists map_1 (K integer, V bigint)",
                      "insert into map_1 values (11,10000001), (22,20000002), (33,30000003)"
    );

    JdbcInterSeance seance = session.openSeance("select * from map_1", null);
    seance.execute();
    JdbcInterCursor<Map<Integer, Long>> cursor =
            seance.openDefaultCursor(Layouts.hashMapOf(Integer.class, Long.class));
    Map<Integer, Long> map = cursor.fetch();

    assertThat(map).containsEntry(11, 10000001L)
                   .containsEntry(22, 20000002L)
                   .containsEntry(33, 30000003L)
                   .hasSize(3);
  }


  private void performStatement(final JdbcInterSession session, final String statementText) {
    JdbcInterSeance seance =
            session.openSeance(statementText, null);
    try {
      seance.execute();
    }
    finally {
      seance.close();
    }
  }

  private void performStatements(final JdbcInterSession session, final String... statementTexts) {
    for (String statementText : statementTexts) {
      performStatement(session, statementText);
    }
  }


  @NotNull
  private JdbcInterSeance query(final String queryText) {
    JdbcInterSession session = openSession();
    JdbcInterSeance seance = session.openSeance(queryText, null);
    seance.execute();
    return seance;
  }


}
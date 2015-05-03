package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

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


  @NotNull
  private JdbcInterSeance query(final String queryText) {JdbcInterSession session = openSession();
    JdbcInterSeance seance = session.openSeance(queryText, null);
    seance.execute();
    return seance;
  }


}
package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.core.Layouts.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseQueryRunnerTest extends BaseHyperSonicFacadeTest {




  private static final class BasicStruct{
    int the_int_value;
    String the_string_value;

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      BasicStruct that = (BasicStruct) o;

      return this.the_int_value == that.the_int_value &&
                     (this.the_string_value == null
                              ? that.the_string_value == null
                              : this.the_string_value.equals(that.the_string_value));
    }

    @Override
    public int hashCode() {
      return the_int_value * 17 + (the_string_value != null ? the_string_value.hashCode() : 0);
    }
  }


  @Test
  public void return_one_basic_struct() {
    final String queryText =
            "select 44 as the_int_value,           \n" +
            "       'million' as the_string_value  \n" +
            "from information_schema.schemata      \n" +
            "limit 1                               \n";
    final SqlQuery<BasicStruct> query =
            new SqlQuery<BasicStruct>(queryText, rowOf(structOf(BasicStruct.class)));

    ourFacade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {

        BasicStruct bs = tran.query(query).run();

        assertThat(bs).isNotNull();
        assertThat(bs.the_int_value).isEqualTo(44);
        assertThat(bs.the_string_value).isEqualTo("million");

      }
    });
  }

  static final String QUERY_RETURNS_TWO_BASIC_STRUCT_ROWS =
          "select distinct 11 as the_int_value,      \n" +
          "                'one' as the_string_value \n" +
          "from information_schema.schemata          \n" +
          "union                                     \n" +
          "select distinct 22 as the_int_value,      \n" +
          "                'two' as the_string_value \n" +
          "from information_schema.schemata          \n" +
          "order by 1                                \n";


  @Test
  public void return_array_of_basic_struct() {
    final SqlQuery<BasicStruct[]> query =
            new SqlQuery<BasicStruct[]>(QUERY_RETURNS_TWO_BASIC_STRUCT_ROWS,
                                        arrayOf(structOf(BasicStruct.class)));

    ourFacade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {

        BasicStruct[] bs = tran.query(query).run();

        assertThat(bs).isNotNull()
                      .hasSize(2);
        assertThat(bs[0].the_int_value).isEqualTo(11);
        assertThat(bs[0].the_string_value).isEqualTo("one");
        assertThat(bs[1].the_int_value).isEqualTo(22);
        assertThat(bs[1].the_string_value).isEqualTo("two");

      }
    });
  }

  @Test
  public void return_list_of_basic_struct() {
    final SqlQuery<List<BasicStruct>> query =
            new SqlQuery<List<BasicStruct>>(QUERY_RETURNS_TWO_BASIC_STRUCT_ROWS,
                                            listOf(structOf(BasicStruct.class)));

    ourFacade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {

        List<BasicStruct> bs = tran.query(query).run();

        assertThat(bs).isNotNull()
                      .hasSize(2);
        assertThat(bs.get(0).the_int_value).isEqualTo(11);
        assertThat(bs.get(0).the_string_value).isEqualTo("one");
        assertThat(bs.get(1).the_int_value).isEqualTo(22);
        assertThat(bs.get(1).the_string_value).isEqualTo("two");

      }
    });
  }


}
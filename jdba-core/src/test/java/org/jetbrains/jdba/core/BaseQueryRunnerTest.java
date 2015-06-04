package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.core.Layouts.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class BaseQueryRunnerTest extends BaseHyperSonicFacadeCase {




  private static final class IntAndString {
    int the_int_value;
    String the_string_value;

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      IntAndString that = (IntAndString) o;

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
    final SqlQuery<IntAndString> query =
            new SqlQuery<IntAndString>(queryText, rowOf(structOf(IntAndString.class)));

    myFacade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {

        IntAndString bs = tran.query(query).run();

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
    final SqlQuery<IntAndString[]> query =
            new SqlQuery<IntAndString[]>(QUERY_RETURNS_TWO_BASIC_STRUCT_ROWS,
                                        arrayOf(structOf(IntAndString.class)));

    myFacade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {

        IntAndString[] bs = tran.query(query).run();

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
    final SqlQuery<List<IntAndString>> query =
            new SqlQuery<List<IntAndString>>(QUERY_RETURNS_TWO_BASIC_STRUCT_ROWS,
                                            listOf(structOf(IntAndString.class)));

    myFacade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {

        List<IntAndString> bs = tran.query(query).run();

        assertThat(bs).isNotNull()
                      .hasSize(2);
        assertThat(bs.get(0).the_int_value).isEqualTo(11);
        assertThat(bs.get(0).the_string_value).isEqualTo("one");
        assertThat(bs.get(1).the_int_value).isEqualTo(22);
        assertThat(bs.get(1).the_string_value).isEqualTo("two");

      }
    });
  }




  @Test
  public void empty_cursor() {
    final SqlQuery<List<Tetra>> query =
        new SqlQuery<List<Tetra>>(
            "select 11,22,33,44 from information_schema.schemata where 1 is null",
            listOf(structOf(Tetra.class)));

    myFacade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {
        List<Tetra> result = session.query(query).run();
        assertThat(result).isEmpty();
      }
    });
  }

  @Test
  public void struct_basic() {
    Tetra struct =
        queryForStruct("select 11 as A, 22 as B, 33 as C, 44 as D from information_schema.schemata",
                       Tetra.class);
    assertThat(struct).isEqualTo(new Tetra(11,22,33,44));
  }

  @Test
  public void struct_fields_disordered() {
    Tetra struct =
        queryForStruct("select 33 as C, 11 as A, 44 as D, 22 as B from information_schema.schemata",
                       Tetra.class);
    assertThat(struct).isEqualTo(new Tetra(11,22,33,44));
  }

  @Test
  public void struct_has_more_fields_than_query() {
    Tetra struct =
        queryForStruct("select 22 as B, 44 as D from information_schema.schemata", Tetra.class);
    assertThat(struct).isEqualTo(new Tetra(0,22,0,44));
  }

  @Test
  public void struct_has_less_fields_than_query() {
    Tetra struct =
        queryForStruct(
            "select 11 as A, 22 as B, 99 as X, 33 as C, 88 as Y, 44 as D from information_schema.schemata",
            Tetra.class);
    assertThat(struct).isEqualTo(new Tetra(11,22,33,44));
  }


  @Test
  public void char_single_1() {
    myFacade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {

        char c =
            tran.query("select 'M' from information_schema.schemata", singleOf(Character.class)).run();

        assertThat(c).isEqualTo('M');

      }
    });
  }

  @Test
  public void char_single_2() {
    myFacade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {

        Character c =
            tran.query("select 'MN' from information_schema.schemata", singleOf(Character.class)).run();

        assertThat(c).isEqualTo('M');

      }
    });
  }

  static class CharStruct {
    char c;
    Character h;
  }

  @Test
  public void char_in_struct() {
    CharStruct s = queryForStruct("select 'X' as c, 'Y' as h from information_schema.schemata", CharStruct.class);
    assert s != null;
    assertThat(s.c).isEqualTo('X');
    assertThat(s.h).isEqualTo('Y');
  }

  @Test
  public void char_in_struct_nulls() {
    CharStruct s = queryForStruct("select null as c, null as h from information_schema.schemata", CharStruct.class);
    assert s != null;
    assertThat(s.c).isEqualTo('\0');
    assertThat(s.h).isNull();
  }


  @Nullable
  private <S> S queryForStruct(final String queryText, final Class<S> structClass) {
    final SqlQuery<S> query = new SqlQuery<S>(queryText, rowOf(structOf(structClass)));
    return myFacade.inTransaction(new InTransaction<S>() {
      @Override
      public S run(@NotNull final DBTransaction tran) {

        return tran.query(query).run();

      }
    });
  }

}
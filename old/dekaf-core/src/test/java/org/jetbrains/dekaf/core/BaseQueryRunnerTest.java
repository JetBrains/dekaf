package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.sql.SqlQuery;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.core.Layouts.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class BaseQueryRunnerTest extends BaseInMemoryDBFacadeCase {




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
            "       'million' as the_string_value  \n";
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
          "union                                     \n" +
          "select distinct 22 as the_int_value,      \n" +
          "                'two' as the_string_value \n" +
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
        queryForStruct("select 11 as A, 22 as B, 33 as C, 44 as D",
                       Tetra.class);
    assertThat(struct).isEqualTo(new Tetra(11,22,33,44));
  }

  @Test
  public void struct_fields_disordered() {
    Tetra struct =
        queryForStruct("select 33 as C, 11 as A, 44 as D, 22 as B",
                       Tetra.class);
    assertThat(struct).isEqualTo(new Tetra(11,22,33,44));
  }

  @Test
  public void struct_has_more_fields_than_query() {
    Tetra struct =
        queryForStruct("select 22 as B, 44 as D", Tetra.class);
    assertThat(struct).isEqualTo(new Tetra(0,22,0,44));
  }

  @Test
  public void struct_has_less_fields_than_query() {
    Tetra struct =
        queryForStruct(
            "select 11 as A, 22 as B, 99 as X, 33 as C, 88 as Y, 44 as D",
            Tetra.class);
    assertThat(struct).isEqualTo(new Tetra(11,22,33,44));
  }


  @Test
  public void char_single_1() {
    myFacade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {

        char c =
            tran.query("select 'M'", singleOf(Character.class)).run();

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
            tran.query("select 'MN'", singleOf(Character.class)).run();

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
    CharStruct s = queryForStruct("select 'X' as c, 'Y' as h", CharStruct.class);
    assert s != null;
    assertThat(s.c).isEqualTo('X');
    assertThat(s.h).isEqualTo('Y');
  }

  @Test
  public void char_in_struct_nulls() {
    CharStruct s = queryForStruct("select null as c, null as h", CharStruct.class);
    assert s != null;
    assertThat(s.c).isEqualTo('\0');
    assertThat(s.h).isNull();
  }


  @Test
  public void array_row_basic() {
    SqlQuery<Integer[]> query =
        new SqlQuery<Integer[]>("select 11, 22, 33",
                                rowOf(arrayOf(3, Integer.class)));
    Integer[] array = query(query);
    assertThat(array).isEqualTo(new Integer[] {11, 22, 33});
  }


  @Test
  public void int_array_basic() {
    SqlQuery<int[]> query =
        new SqlQuery<int[]>("select 11, 22, 33",
                                rowOf(arrayOfInts()));
    int[] array = query(query);
    assertThat(array).containsExactly(11,22,33);
  }

  @Test
  public void long_array_basic() {
    SqlQuery<long[]> query =
        new SqlQuery<long[]>("select 11, 22, 33",
                                rowOf(arrayOfLongs()));
    long[] array = query(query);
    assertThat(array).containsExactly(11L,22L,33L);
  }


  @Test
  public void million_of_ints() {
    SqlQuery<int[]> query =
        new SqlQuery<int[]>("select cast(X as int) from system_range(1,1000000)", columnOfInts(1000));

    int[] array = query(query);
    assertThat(array).hasSize(1000000);
    assertThat(array[0]).isEqualTo(1);
    assertThat(array[999999]).isEqualTo(1000000);
  }

  @Test
  public void million_of_longs() {
    SqlQuery<long[]> query =
        new SqlQuery<long[]>("select cast(X as bigint) from system_range(1,1000000)", columnOfLongs(1000));

    long[] array = query(query);
    assertThat(array).hasSize(1000000);
    assertThat(array[0]).isEqualTo(1L);
    assertThat(array[999999]).isEqualTo(1000000L);
  }


  @Nullable
  private <S> S queryForStruct(final String queryText, final Class<S> structClass) {
    final SqlQuery<S> query = new SqlQuery<S>(queryText, rowOf(structOf(structClass)));
    return query(query);
  }


  private <X> X query(final SqlQuery<X> query) {
    return myFacade.inTransaction(new InTransaction<X>() {
      @Override
      public X run(@NotNull final DBTransaction tran) {

        return tran.query(query).run();

      }
    });
  }

}
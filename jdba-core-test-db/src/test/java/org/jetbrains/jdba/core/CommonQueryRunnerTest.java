package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.CommonIntegrationCase;
import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.core.Layouts.*;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class CommonQueryRunnerTest extends CommonIntegrationCase {

  protected static boolean isOracle =
      DB.rdbms().code.equalsIgnoreCase("ORACLE");


  @Before
  public void setUp() throws Exception {
    DB.connect();
    TH.prepareX1();
    TH.prepareX1000();
    TH.prepareX1000000();
  }


  public static class PrimitiveNumbers {
    byte  B;
    short S;
    int   I;
    long  L;
  }

  public static class BoxedNumbers {
    Byte    B;
    Short   S;
    Integer I;
    Long    L;
  }


  @Test
  public void query_existence_0 () {
    String queryText = "select 1 from "+(isOracle ? "dual" : "X1")+" where 1 is null";
    SqlQuery<Boolean> q = new SqlQuery<Boolean>(queryText, existence());
    final Boolean b = query(q);
    assertThat(b).isNotNull()
                 .isFalse();
  }

  @Test
  public void query_existence_1() {
    String queryText = "select 1 "+(isOracle ? "from dual" : "");
    SqlQuery<Boolean> q = new SqlQuery<Boolean>(queryText, existence());
    final Boolean b = query(q);
    assertThat(b).isNotNull()
                 .isTrue();
  }

  @Test
  public void query_primitive_numbers_positive() {
    final String queryText =
        "select 127 as B, 32767 as S, 2147483647 as I, 9223372036854775807 as L from X1";
    final SqlQuery<PrimitiveNumbers> query =
        new SqlQuery<PrimitiveNumbers>(queryText, rowOf(structOf(PrimitiveNumbers.class)));
    PrimitiveNumbers pn = query(query);

    assertThat(pn.B).isEqualTo((byte)127);
    assertThat(pn.S).isEqualTo((short)32767);
    assertThat(pn.I).isEqualTo(2147483647);
    assertThat(pn.L).isEqualTo(9223372036854775807L);
  }

  @Test
  public void query_primitive_numbers_negative() {
    final String queryText =
        "select -128 as B, -32768 as S, -2147483648 as I, -9223372036854775808 as L from X1";
    final SqlQuery<PrimitiveNumbers> query =
        new SqlQuery<PrimitiveNumbers>(queryText, rowOf(structOf(PrimitiveNumbers.class)));
    PrimitiveNumbers pn = query(query);

    assertThat(pn.B).isEqualTo((byte)-128);
    assertThat(pn.S).isEqualTo((short)-32768);
    assertThat(pn.I).isEqualTo(-2147483648);
    assertThat(pn.L).isEqualTo(-9223372036854775808L);
  }

  @Test
  public void query_boxed_numbers_positive() {
    final String queryText =
        "select 127 as B, 32767 as S, 2147483647 as I, 9223372036854775807 as L from X1";
    final SqlQuery<BoxedNumbers> query =
        new SqlQuery<BoxedNumbers>(queryText, rowOf(structOf(BoxedNumbers.class)));
    BoxedNumbers bn = query(query);

    assertThat(bn.B).isEqualTo((byte) 127);
    assertThat(bn.S).isEqualTo((short)32767);
    assertThat(bn.I).isEqualTo(2147483647);
    assertThat(bn.L).isEqualTo(9223372036854775807L);
  }


  @Test
  public void query_raw_numbers() {
    final String queryText =
        "select 127 as B, 32767 as S, 2147483647 as I, 9223372036854775807 as L from X1";
    final SqlQuery<Object[]> query =
        new SqlQuery<Object[]>(queryText, rowOf(rawArray()));
    final Object[] numbers = query(query);

    assertThat(numbers).hasSize(4);
    assertThat(numbers[0]).isInstanceOf(Number.class);
    assertThat(numbers[1]).isInstanceOf(Number.class);
    assertThat(numbers[2]).isInstanceOf(Number.class);
    assertThat(numbers[3]).isInstanceOf(Number.class);
    assertThat(((Number)numbers[0]).intValue()).isEqualTo(127);
    assertThat(((Number)numbers[1]).intValue()).isEqualTo(32767);
    assertThat(((Number)numbers[2]).intValue()).isEqualTo(2147483647);
    assertThat(((Number)numbers[3]).longValue()).isEqualTo(9223372036854775807L);
  }

  @Test
  public void query_raw_strings() {
    final String queryText =
        "select 'C', 'String' from X1";
    final SqlQuery<Object[]> query =
        new SqlQuery<Object[]>(queryText, rowOf(rawArray()));
    final Object[] strings = query(query);

    assertThat(strings).hasSize(2);

    assertThat(strings[0]).isInstanceOf(String.class);
    assertThat(strings[1]).isInstanceOf(String.class);

    assertThat(strings[0]).isEqualTo("C");
    assertThat(strings[1]).isEqualTo("String");
  }



  @Test
  public void query_1000_values() {
    List<Integer> values =
        DB.inTransaction(new InTransaction<List<Integer>>() {
          @Override
          public List<Integer> run(@NotNull final DBTransaction tran) {
            return tran.query("select X from X1000 order by 1", listOf(oneOf(Integer.class))).run();
          }
        });
    assertThat(values).isNotNull()
                      .hasSize(1000)
                      .contains(1,2,3,4,998,999,1000);
  }


  @Test
  public void query_1000000_values() {
    List<Integer> values =
        DB.inTransaction(new InTransaction<List<Integer>>() {
          @Override
          public List<Integer> run(@NotNull final DBTransaction tran) {
            return tran.query("select X from X1000000 order by 1", listOf(oneOf(Integer.class))).run();
          }
        });
    assertThat(values).isNotNull()
                      .hasSize(1000000)
                      .contains(1,2,3,4,999998,999999,1000000);
  }


  protected <T> T query(@NotNull final SqlQuery<T> query) {
    return DB.inTransaction(new InTransaction<T>() {
      @Override
      public T run(@NotNull final DBTransaction tran) {
        return tran.query(query).run();
      }
    });
  }

}

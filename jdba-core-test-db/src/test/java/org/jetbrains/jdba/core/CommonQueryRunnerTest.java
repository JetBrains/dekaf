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

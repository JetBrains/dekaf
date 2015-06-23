package org.jetbrains.jdba.core;

import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.core.Layouts.singleOf;


/**
 * @author Leonid Bushuev from JetBrains
 **/
public class PostgreQueryRunnerTest extends CommonQueryRunnerTest {

  @Test
  public void query_boolean_as_boolean() {
    SqlQuery<Boolean> q = new SqlQuery<Boolean>("select true",singleOf(Boolean.class));
    final Boolean b = query(q);
    assertThat(b).isTrue();
  }

  @Test
  public void query_boolean_as_int() {
    SqlQuery<Byte> q = new SqlQuery<Byte>("select true",singleOf(Byte.class));
    final Byte b = query(q);
    assertThat(b).isEqualTo((byte)1);
  }

  @Test
  public void query_int_as_boolean() {
    SqlQuery<Boolean> q = new SqlQuery<Boolean>("select 1",singleOf(Boolean.class));
    final Boolean b = query(q);
    assertThat(b).isTrue();
  }


  @Test
  public void query_array_of_bytes_as_bytes() {
    String queryText = "select '{1,2,3,4}'::smallint[]";
    SqlQuery<byte[]> q = new SqlQuery<byte[]>(queryText, singleOf(byte[].class));
    byte[] bytes = query(q);
    assertThat(bytes).containsExactly((byte)1,(byte)2,(byte)3,(byte)4);
  }

  @Test
  public void query_array_of_shorts_as_shorts() {
    String queryText = "select '{1,2,3,4}'::smallint[]";
    SqlQuery<short[]> q = new SqlQuery<short[]>(queryText, singleOf(short[].class));
    short[] shorts = query(q);
    assertThat(shorts).containsExactly((short)1,(short)2,(short)3,(short)4);
  }

  @Test
  public void query_array_of_ints_as_ints() {
    String queryText = "select '{1,2,3,4,5}'::int[]";
    SqlQuery<int[]> q = new SqlQuery<int[]>(queryText, singleOf(int[].class));
    int[] ints = query(q);
    assertThat(ints).containsExactly(1,2,3,4,5);
  }

  @Test
  public void query_array_of_ints_as_string() {
    String queryText = "select '{1,2,3,4,5}'::int[]";
    SqlQuery<String> q = new SqlQuery<String>(queryText, singleOf(String.class));
    String string = query(q);
    assertThat(string).isEqualTo("{1,2,3,4,5}");
  }

  @Test
  public void query_array_of_ints_as_NumberArray() {
    String queryText = "select '{1,2,3,4,5}'::int[]";
    SqlQuery<Number[]> q = new SqlQuery<Number[]>(queryText, singleOf(Number[].class));
    Number[] array = query(q);
    assertThat(array).hasSize(5)
                     .extractingResultOf("intValue").containsExactly(1,2,3,4,5);
  }


  @Test
  public void query_array_of_longs_as_longs() {
    String queryText = "select '{1,2,3,4,5,6,7}'::bigint[]";
    SqlQuery<long[]> q = new SqlQuery<long[]>(queryText, singleOf(long[].class));
    long[] longs = query(q);
    assertThat(longs).containsExactly(1L,2L,3L,4L,5L,6L,7L);
  }


  @Test
  public void query_array_of_strings() {
    String queryText = "select '{-Subaru-,-BMW-,-Volga-,-Lada-}'::varchar(6)[]".replace('-', '"');
    SqlQuery<String[]> q = new SqlQuery<String[]>(queryText, singleOf(String[].class));
    String[] strings = query(q);
    assertThat(strings).containsExactly("Subaru","BMW","Volga","Lada");
  }


}

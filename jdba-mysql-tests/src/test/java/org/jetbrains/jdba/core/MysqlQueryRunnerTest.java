package org.jetbrains.jdba.core;

import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.core.Layouts.singleOf;


/**
 * @author Leonid Bushuev from JetBrains
 **/
public class MysqlQueryRunnerTest extends CommonQueryRunnerTest {

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

}

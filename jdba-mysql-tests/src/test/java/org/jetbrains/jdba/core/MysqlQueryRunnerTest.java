package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.SqlQuery;
import org.jetbrains.jdba.util.Strings;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.core.Layouts.singleOf;


/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
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

  @Test
  public void query_varchar255() {
    final String str255 = Strings.repeat("0123456789", null, 26).substring(0, 255);
    assert str255.length() == 255;
    query_string(str255);
  }

  @Test
  public void query_varchar65535() {
    final String str65535 = Strings.repeat("0123456789", null, 6554).substring(0, 65535);
    assert str65535.length() == 65535;
    query_string(str65535);
  }

  private void query_string(@NotNull final String string) {
    final String query = "select '" + string + "' as test_str";
    String result =
        DB.inTransaction(new InTransaction<String>() {
          @Override
          public String run(@NotNull final DBTransaction tran) {

            return tran.query(query, singleOf(String.class)).run();

          }
        });

    assertThat(result).isEqualTo(string);
  }

}

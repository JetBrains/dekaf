package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.ImplementationAccessibleService.Names;
import org.jetbrains.dekaf.jdbc.MysqlConsts;
import org.jetbrains.dekaf.jdbc.MysqlIntermediateFacade;
import org.jetbrains.dekaf.sql.SqlQuery;
import org.jetbrains.dekaf.util.Strings;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.core.Layouts.columnOfInts;
import static org.jetbrains.dekaf.core.Layouts.singleOf;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class MysqlQueryRunnerTest extends CommonQueryRunnerTest {


  @After
  public void tearDown() throws Exception {
    getIntermediateFacade().setFetchStrategy(MysqlConsts.FETCH_STRATEGY_AUTO);
  }


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


  @Test
  public void fetch_1000000_rowStrategy() {
    getIntermediateFacade().setFetchStrategy(MysqlConsts.FETCH_STRATEGY_ROW);
    retrieveMillion(true);
  }

  @Test
  public void fetch_1000000_wholeStrategy() {
    getIntermediateFacade().setFetchStrategy(MysqlConsts.FETCH_STRATEGY_WHOLE);
    retrieveMillion(false);
  }

  private void retrieveMillion(boolean rowMode) {
    final int[] x = new int[1];

    int[] values =
      DB.inTransaction(tran -> {
          DBQueryRunner<int[]> queryRunner =
              tran.query("select X from X1000000 order by 1", columnOfInts(1000000));
          int[] vals = queryRunner.run();
          x[0] = getStatementFetchSize(queryRunner);
          return vals;
      });

    assertThat(values).isNotNull()
                      .hasSize(1000000)
                      .contains(1,2,3,4,999998,999999,1000000);

    if (rowMode) {
      assertThat(x[0]).isEqualTo(Integer.MIN_VALUE);
    }
    else {
      assertThat(x[0]).isNotEqualTo(Integer.MIN_VALUE);
    }
  }

  @Test
  public void fetch_1000000_packs() {
    getIntermediateFacade().setFetchStrategy(MysqlConsts.FETCH_STRATEGY_AUTO);

    final int[] x = new int[1];

    final int[] result = new int[1000000];

    DB.inTransactionDo(tran -> {

        DBQueryRunner<int[]> queryRunner = tran.query("select X from X1000000 order by 1",
                                                columnOfInts(1000));
        queryRunner.packBy(1000);
        int offset = 0;
        int[] pack = queryRunner.run();
        x[0] = getStatementFetchSize(queryRunner);
        while (pack != null && pack.length > 0) {
          System.arraycopy(pack, 0, result, offset, pack.length);
          offset += pack.length;
          pack = queryRunner.nextPack();
        }

    });

    assertThat(result).contains(1,2,3,4,999998,999999,1000000);
    assertThat(x[0]).isEqualTo(Integer.MIN_VALUE);
  }

  private static int getStatementFetchSize(final @NotNull DBQueryRunner<?> queryRunner) {
    java.sql.Statement statement =
        queryRunner.getSpecificService(java.sql.Statement.class, Names.JDBC_STATEMENT);
    assert statement != null;
    try {
      return statement.getFetchSize();
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }



  private void query_string(@NotNull final String string) {
    final String query = "select '" + string + "' as test_str";
    String result =
        DB.inTransaction(tran -> {

            return tran.query(query, singleOf(String.class)).run();

        });

    assertThat(result).isEqualTo(string);
  }

  @NotNull
  @Override
  protected String queryCalendarValuesFromParameters() {
    return "select ? as javaDate, date(?) as sqlDate, ? as sqlTimestamp, time(?) as sqlTime";
  }


  @NotNull
  private static MysqlIntermediateFacade getIntermediateFacade() {
    MysqlIntermediateFacade intermediateFacade =
        DB.getSpecificService(MysqlIntermediateFacade.class,
                              Names.INTERMEDIATE_SERVICE);
    assert intermediateFacade != null;
    return intermediateFacade;
  }


}

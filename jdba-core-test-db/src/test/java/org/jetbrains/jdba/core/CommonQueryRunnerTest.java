package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.CommonIntegrationCase;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.core.Layouts.listOf;
import static org.jetbrains.jdba.core.Layouts.oneOf;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class CommonQueryRunnerTest extends CommonIntegrationCase {

  @Before
  public void setUp() throws Exception {
    DB.connect();
    TH.prepareX1000();
    TH.prepareX1000000();
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

}

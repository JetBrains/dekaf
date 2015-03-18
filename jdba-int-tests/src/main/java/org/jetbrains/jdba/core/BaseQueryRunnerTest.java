package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.SQLQuery;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import testing.categories.ForEveryRdbms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.TestDB2.FACADE;


@Category(ForEveryRdbms.class)
@FixMethodOrder(MethodSorters.JVM)
public class BaseQueryRunnerTest {


  @Test
  public void query_1_inSession() {
    // TODO condition
    final String ourSelect1 = FACADE.rdbms().code.equalsIgnoreCase("ORACLE") ? "select 1 from dual" : "select 1";

    final SQLQuery<Integer> query = FACADE.sql().query(ourSelect1, RowsCollectors.oneRow(Integer.class));
    FACADE.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {
        Integer result = session.query(query).run();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1);
      }
    });
  }

  @Test
  public void query_1_inTransaction() {
    final String ourSelect1 = FACADE.rdbms().code.equalsIgnoreCase("ORACLE") ? "select 1 from dual" : "select 1";
    final SQLQuery<Integer> query = FACADE.sql().query(ourSelect1, RowsCollectors.oneRow(Integer.class));
    FACADE.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull DBTransaction transaction) {
        Integer result = transaction.query(query).run();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1);
      }
    });
  }


}
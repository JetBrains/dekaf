package org.jetbrains.jdba.core1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.junitft.FineRunner;
import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
@RunWith(FineRunner.class)
public class CommonBasicIntTest extends CommonIntegrationCase {

  @Test
  public void select_1_inTransaction() {
    final SqlQuery<Integer> query1 = getSelect1();

    db.facade.inTransaction(new InTransactionNoResult() {
      public void run(@NotNull final DBTransaction tran) {

        Integer value = tran.query(query1).run();

        assertThat(value).isEqualTo(1);

      }
    });
  }

  @Test
  public void select_1_inSession() {
    final SqlQuery<Integer> query1 = getSelect1();

    db.facade.inSession(new InSessionNoResult() {
      public void run(@NotNull final DBSession session) {

        Integer value = session.query(query1).run();

        assertThat(value).isEqualTo(1);

      }
    });
  }

  @NotNull
  private SqlQuery<Integer> getSelect1() {
    String queryText = "select 1";
    if (isOracle) queryText += " from dual";

    return new SqlQuery<Integer>(queryText, RowsCollectors.oneRow(Integer.class));
  }


}

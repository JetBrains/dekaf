package org.jetbrains.jdba.core1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.junitft.FineRunner;
import org.jetbrains.jdba.sql.SQLQuery;
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
    final SQLQuery<Integer> query1 = getSelect1();

    db.facade.inTransaction(new InTransactionNoResult() {
      public void run(@NotNull final DBTransaction tran) {

        Integer value = tran.query(query1).run();

        assertThat(value).isEqualTo(1);

      }
    });
  }

  @Test
  public void select_1_inSession() {
    final SQLQuery<Integer> query1 = getSelect1();

    db.facade.inSession(new InSessionNoResult() {
      public void run(@NotNull final DBSession session) {

        Integer value = session.query(query1).run();

        assertThat(value).isEqualTo(1);

      }
    });
  }

  @NotNull
  private SQLQuery<Integer> getSelect1() {
    String queryText = "select 1";
    if (isOracle) queryText += " from dual";

    return new SQLQuery<Integer>(queryText, RowsCollectors.oneRow(Integer.class));
  }


}

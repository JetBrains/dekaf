package org.jetbrains.jdba.postgre;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.DBTransaction;
import org.jetbrains.jdba.core.InTransactionNoResult;
import org.jetbrains.jdba.core.RowsCollectors;
import org.jetbrains.jdba.sql.SQLQuery;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class PostgreFacadeTest extends PostgreIntegrationCase {

  @Test
  public void connect_basic() {
    assertThat(db.facade.isConnected()).isTrue();
  }

  @Test
  public void select_1() {
    final SQLQuery<Integer> query1 =
            new SQLQuery<Integer>("select 1", RowsCollectors.oneRow(Integer.class));

    db.facade.inTransaction(new InTransactionNoResult() {
      public void run(@NotNull final DBTransaction tran) {

        Integer value = tran.query(query1).run();

        assertThat(value).isEqualTo(1);

      }
    });
  }




}

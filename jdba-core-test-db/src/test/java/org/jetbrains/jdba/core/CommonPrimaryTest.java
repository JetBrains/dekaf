package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.CommonIntegrationCase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public abstract class CommonPrimaryTest extends CommonIntegrationCase {

  @Test
  public void connect_disconnect() {
    assert DB != null;

    DB.connect();
    assertThat(DB.isConnected()).isTrue();

    DB.disconnect();
    assertThat(DB.isConnected()).isFalse();
  }

  @Test
  public void select_1_in_session() {
    assert DB != null;

    DB.connect();
    DB.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        final Integer v = session.query("select 1 " + fromSingleRowTable(),
                                          Layouts.singleOf(Integer.class))
                                   .run();
        assertThat(v).isNotNull()
                     .isEqualTo(1);

      }
    });
  }

  @Test
  public void select_1_in_transaction() {
    assert DB != null;

    DB.connect();
    DB.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {

        final Integer v = tran.query("select 1 " + fromSingleRowTable(),
                                        Layouts.singleOf(Integer.class))
                                 .run();
        assertThat(v).isNotNull()
                     .isEqualTo(1);

      }
    });
  }

  @NotNull
  protected String fromSingleRowTable() {
    return "";
  }

}

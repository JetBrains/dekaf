package org.jetbrains.jdba.postgre;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.DBTransaction;
import org.jetbrains.jdba.core.InTransactionNoResult;
import org.jetbrains.jdba.core.RowsCollectors;
import org.jetbrains.jdba.sql.SQLQuery;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class PostgreBitBoolTest extends PostgreIntegrationCase {

  @BeforeClass
  public static void setupClass() {
    connectToDB();
  }

  @Test
  public void get_bit_as_boolean() {
    get_X_as_boolean("select 1::bit");
  }

  @Test
  public void get_bit_as_integer() {
    get_X_as_integer("select 1::bit");
  }

  @Test
  public void get_boolean_as_boolean() {
    get_X_as_boolean("select 42 > 33");
  }

  @Test
  public void get_boolean_as_integer() {
    get_X_as_integer("select 42 > 33");
  }

  private void get_X_as_boolean(final String queryText) {
    final SQLQuery<Boolean> query =
            new SQLQuery<Boolean>(queryText, RowsCollectors.oneRow(Boolean.class));

    db.facade.inTransaction(new InTransactionNoResult() {
      public void run(@NotNull final DBTransaction tran) {

        Boolean bit = tran.query(query).run();

        assertThat(bit).isNotNull()
                       .isTrue();

      }
    });
  }

  private void get_X_as_integer(final String queryText) {
    final SQLQuery<Integer> query =
            new SQLQuery<Integer>(queryText, RowsCollectors.oneRow(Integer.class));

    db.facade.inTransaction(new InTransactionNoResult() {
      public void run(@NotNull final DBTransaction tran) {

        Integer bit = tran.query(query).run();

        assertThat(bit).isNotNull()
                       .isEqualTo(1);

      }
    });
  }


}

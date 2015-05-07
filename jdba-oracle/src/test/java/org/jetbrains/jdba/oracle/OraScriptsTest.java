package org.jetbrains.jdba.oracle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core1.DBTransaction;
import org.jetbrains.jdba.core1.InTransactionNoResult;
import org.jetbrains.jdba.core1.RowsCollectors;
import org.jetbrains.jdba.sql.SQLQuery;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import testing.categories.ForOracle;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Category(ForOracle.class)
@FixMethodOrder(MethodSorters.JVM)
public class OraScriptsTest extends OracleIntegrationCase {


  @Test
  public void isConnected() {
    assertThat(db.facade.isConnected()).isTrue();
  }

  @Test
  public void select_1() {
    final SQLQuery<Integer> query1 =
            new SQLQuery<Integer>("select 1 from dual", RowsCollectors.oneRow(Integer.class));

    db.facade.inTransaction(new InTransactionNoResult() {
      public void run(@NotNull final DBTransaction tran) {

        Integer value = tran.query(query1).run();

        assertThat(value).isEqualTo(1);

      }
    });
  }


  /*
  final OraSQL mySQL = prepareSQL();


  @BeforeClass
  public static void setUpClass() throws Exception {
    FACADE.connect();
  }


  private static OraSQL prepareSQL() {
    OraSQL sql = new OraSQL();
    sql.assignResources(OraScriptsTest.class);
    return sql;
  }


  @Test
  public void run_script() {
    zapSchema();

    final SQLScript script = mySQL.script("##simple-ddl-script");
    final SQLQuery<List<String>> query = mySQL.query("select Note from Topic order by Id", RowsCollectors.list(String.class));
    FACADE.inSession(new InSessionNoResult() {
      public void run(@NotNull DBSession session) {
        session.script(script).run();
        List<String> notes = session.query(query).run();
        assertThat(notes).hasSize(2);
        assertThat(notes.get(0)).isEqualTo("The first topic");
        assertThat(notes.get(1)).isEqualTo("The second topic");
      }
    });
  }
  */


}

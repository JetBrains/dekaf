package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.CommonIntegrationCase;
import org.jetbrains.jdba.sql.Scriptum;
import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class MysqlTestHelperTest extends CommonIntegrationCase {

  private static final Scriptum ourScriptum =
      Scriptum.of(MysqlTestHelperTest.class);


  @BeforeClass
  public static void connect() {
    DB.connect();
    TH.zapSchema();
  }


  @Test
  public void zap_table_myIsam() {
    // create a table
    TH.ensureNoTableOrView("simple_table_1");
    TH.performScript(ourScriptum, "SimpleTableMyIsam");

    // ensure that we can detect the sequence existence
    assertThat(tableExists("simple_table_1")).isTrue();

    // zap it
    TH.zapSchema();

    // ensure that the sequence is dropped
    assertThat(tableExists("simple_table_1")).isFalse();
  }

  @Test
  public void zap_table_innoDB() {
    // create a table
    TH.ensureNoTableOrView("simple_table_2");
    TH.performScript(ourScriptum, "SimpleTableInnoDB");

    // ensure that we can detect the sequence existence
    assertThat(tableExists("simple_table_2")).isTrue();

    // zap it
    TH.zapSchema();

    // ensure that the sequence is dropped
    assertThat(tableExists("simple_table_2")).isFalse();
  }


  @Test
  public void zap_tables_with_cyclic_FK() {
    // create a table
    TH.ensureNoTableOrView("Tab_A", "Tab_B");
    TH.performScript(ourScriptum, "CyclicFK");

    // ensure that we can detect the sequence existence
    assertThat(tableExists("Tab_A")).isTrue();
    assertThat(tableExists("Tab_B")).isTrue();

    // zap it
    TH.zapSchema();

    // ensure that the sequence is dropped
    assertThat(tableExists("Tab_A")).isFalse();
    assertThat(tableExists("Tab_B")).isFalse();
  }


  private static final SqlQuery<Boolean> CHECK_TABLE_EXISTS_QUERY =
      ourScriptum.query("CheckTableExists", Layouts.existence());

  private boolean tableExists(final String tableName) {
    return DB.inTransaction(new InTransaction<Boolean>() {
      @Override
      public Boolean run(@NotNull final DBTransaction tran) {
        return tran.query(CHECK_TABLE_EXISTS_QUERY).withParams(tableName).run();
      }
    });
  }


}

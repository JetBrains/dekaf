package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.CommonIntegrationCase;
import org.jetbrains.dekaf.sql.Scriptum;
import org.jetbrains.dekaf.sql.SqlQuery;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@SuppressWarnings("Duplicates")
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

    // ensure that we can detect both tables existence
    assertThat(tableExists("Tab_A")).isTrue();
    assertThat(tableExists("Tab_B")).isTrue();

    // zap it
    TH.zapSchema();

    // ensure that the tables are dropped
    assertThat(tableExists("Tab_A")).isFalse();
    assertThat(tableExists("Tab_B")).isFalse();
  }


  @Test
  public void zap_procedure() {
    // create a procedure
    TH.zapSchema();
    TH.performScript(ourScriptum, "Pro42");

    // ensure that we can detect the procedure existence
    assertThat(routineExists("Pro42")).isTrue();

    // zap it
    TH.zapSchema();

    // ensure that the procedure is dropped
    assertThat(routineExists("Pro42")).isFalse();
  }


  @Test
  public void zap_function() {
    // create a function
    TH.zapSchema();
    TH.performScript(ourScriptum, "Fun33");

    // ensure that we can detect the function existence
    assertThat(routineExists("Fun33")).isTrue();

    // zap it
    TH.zapSchema();

    // ensure that the function is dropped
    assertThat(routineExists("Fun33")).isFalse();
  }


  @Test
  public void zap_table_fun_view() {
    // create a table
    TH.zapSchema();
    TH.performScript(ourScriptum, "TableFunView");

    // ensure that we can detect objects existence
    assertThat(tableExists("T1")).isTrue();
    assertThat(tableExists("V1")).isTrue();
    assertThat(routineExists("fun_t1")).isTrue();

    // zap it
    TH.zapSchema();

    // ensure that the they are dropped
    assertThat(tableExists("T1")).isFalse();
    assertThat(tableExists("V1")).isFalse();
    assertThat(routineExists("fun_t1")).isFalse();
  }



  private static final SqlQuery<Boolean> CHECK_TABLE_EXISTS_QUERY =
      ourScriptum.query("CheckTableExists", Layouts.existence());

  private static final SqlQuery<Boolean> CHECK_ROUTINE_EXISTS_QUERY =
      ourScriptum.query("CheckRoutineExists", Layouts.existence());


  private boolean tableExists(final String tableName) {
    return DB.inTransaction(new InTransaction<Boolean>() {
      @Override
      public Boolean run(@NotNull final DBTransaction tran) {
        return tran.query(CHECK_TABLE_EXISTS_QUERY).withParams(tableName).run();
      }
    });
  }

  private boolean routineExists(final String routineName) {
    return DB.inTransaction(new InTransaction<Boolean>() {
      @Override
      public Boolean run(@NotNull final DBTransaction tran) {
        return tran.query(CHECK_ROUTINE_EXISTS_QUERY).withParams(routineName).run();
      }
    });
  }


}

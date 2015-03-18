package org.jetbrains.jdba.oracle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.junitft.FineRunner;
import org.jetbrains.jdba.junitft.TestWithParams;
import org.jetbrains.jdba.sql.SQL;
import org.jetbrains.jdba.sql.SQLCommand;
import org.jetbrains.jdba.sql.SQLScript;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
@RunWith(FineRunner.class)
public class OraSQLTest {



  public static final String[][] PL_SCENARIOS = {
      { "ora-pl-scenario-begin" },
      { "ora-pl-scenario-declare" },
      { "ora-pl-scenario-create-procedure"},
  };

  @TestWithParams(params = "PL_SCENARIOS")
  public void pl_scenario(String name) {
    SQL sql = getSQL();

    SQLScript script = sql.script("##"+name);

    assertThat((Integer)script.getCommands().size()).isEqualTo((Integer)1);
  }


  public static final String[][] SQL_SCRIPTS_FOR_SPLITTING = {
      { "ora-sql-script-slash" },
      { "ora-sql-script-semicolon-1" },
      { "ora-sql-script-semicolon-2" },
  };

  @TestWithParams(params = "SQL_SCRIPTS_FOR_SPLITTING")
  public void sql_script_splitting(String name) {
    SQL sql = getSQL();

    SQLScript script = sql.script("##"+name);

    assertThat((Integer)script.getCommands().size()).isEqualTo((Integer)4);

    for (SQLCommand command : script.getCommands()) {
      assertThat(command).isNotNull();
      assertThat(command.getSourceText()).isNotEmpty();
    }
  }

  @TestWithParams(params = "SQL_SCRIPTS_FOR_SPLITTING")
  public void sql_script_truncating(String name) {
    SQL sql = getSQL();

    SQLScript script = sql.script("##"+name);

    for (SQLCommand command : script.getCommands()) {
      final String text = command.getSourceText();
      assertThat(text).doesNotMatch("^\\s.*$")
                      .doesNotMatch("^.*\\s$");
    }
  }

  @Test
  public void mixed_script() {
    SQL sql = getSQL();

    SQLScript script = sql.script("##ora-mixed-script");

    assertThat((Integer)script.getCommands().size()).isEqualTo((Integer)7);
  }


  @NotNull
  private SQL getSQL() {
    SQL sql = new OraSQL();
    sql.assignResources(OraSQLTest.class.getClassLoader(), "oracle");
    return sql;
  }

}

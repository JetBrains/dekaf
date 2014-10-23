package org.jetbrains.dba.scripts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.access.DBSession;
import org.jetbrains.dba.access.InSessionNoResult;
import org.jetbrains.dba.access.RowsCollectors;
import org.jetbrains.dba.sql.OraSQL;
import org.jetbrains.dba.sql.SQLQuery;
import org.jetbrains.dba.sql.SQLScript;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testing.categories.ForOracle;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dba.TestDB.FACADE;
import static org.jetbrains.dba.TestDB.zapSchema;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Category(ForOracle.class)
public class OraScriptsTest {

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
      @Override
      public void run(@NotNull DBSession session) {
        session.script(script).run();
        List<String> notes = session.query(query).run();
        assertThat(notes).hasSize(2);
        assertThat(notes.get(0)).isEqualTo("The first topic");
        assertThat(notes.get(1)).isEqualTo("The second topic");
      }
    });
  }


}

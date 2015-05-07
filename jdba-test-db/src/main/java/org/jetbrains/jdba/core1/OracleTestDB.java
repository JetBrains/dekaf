package org.jetbrains.jdba.core1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.SqlQuery;
import org.jetbrains.jdba.sql.SqlScriptBuilder;
import org.jetbrains.jdba.util.Strings;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class OracleTestDB extends BaseTestDB {


  OracleTestDB(@NotNull final DBFacade facade) {
    super(facade);
  }


  @Override
  public void ensureNoTables(final String... names) {
    final int n = names != null ? names.length : 0;
    if (n == 0) return;

    final String queryText = "select table_name from tabs where table_name in ("
                           + Strings.repeat("upper(?)", ",", n)
                           + ")";
    final SqlQuery<String[]> query = new SqlQuery<String[]>(queryText, RowsCollectors.array(String.class));

    facade.inSession(new InSessionNoResult() {
      public void run(@NotNull final DBSession session) {

        final String[] namesToDrop =
                session.query(query).withParams(names).run();

        if (namesToDrop.length > 0) {
          SqlScriptBuilder b = new SqlScriptBuilder();
          for (String name : namesToDrop) {
            b.add("drop table "+name+" cascade constraints");
          }
          session.script(b.build()).run();
        }

      }
    });
  }

}

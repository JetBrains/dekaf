package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.SQLQuery;
import org.jetbrains.jdba.sql.SQLScriptBuilder;
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
    final SQLQuery<String[]> query = new SQLQuery<String[]>(queryText, RowsCollectors.array(String.class));

    facade.inSession(new InSessionNoResult() {
      public void run(@NotNull final DBSession session) {

        final String[] namesToDrop =
                session.query(query).withParams(names).run();

        if (namesToDrop.length > 0) {
          SQLScriptBuilder b = new SQLScriptBuilder();
          for (String name : namesToDrop) {
            b.add("drop table "+name+" cascade constraints");
          }
          session.script(b.build()).run();
        }

      }
    });
  }

}

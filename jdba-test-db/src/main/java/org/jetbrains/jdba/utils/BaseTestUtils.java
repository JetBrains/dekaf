package org.jetbrains.jdba.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.*;
import org.jetbrains.jdba.sql.SQL;
import org.jetbrains.jdba.sql.SQLQuery;

import static org.jetbrains.jdba.utils.Strings.replace;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class BaseTestUtils {

  protected final DBFacade facade;
  protected final SQL sql;


  public BaseTestUtils(@NotNull final DBFacade facade) {
    this.facade = facade;
    this.sql = facade.sql();
  }


  @NotNull
  public String nameCatalogToScript(@NotNull final String nameFromCatalog) {
    return nameIsSafe(nameFromCatalog) ? nameFromCatalog : '"' + nameFromCatalog + '"';
  }


  /**
   * Checks whether this name met both script and catalog rules.
   * @param name  a name to check.
   * @return      true if it can be used in both case - in catalog and in scripts.
   */
  public abstract boolean nameIsSafe(@NotNull final String name);


  public <D> D query(@NotNull final String queryText,
                     @NotNull final DBRowsCollector<D> collector,
                     final Object... params) {
    SQLQuery<D> query = sql.query(queryText, collector);
    return query(query, params);
  }

  public <D> D query(@NotNull final SQLQuery<D> query, final Object... params) {
    return facade.inTransaction(new InTransaction<D>() {
      @Override
      public D run(@NotNull DBTransaction tran) {
        return tran.query(query).withParams(params).run();
      }
    });
  }


  public void run(@NotNull final String cmd) {
    facade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {
        session.command(cmd).run();
      }
    });
  }



  public void ensureNoTablesLike(final String... names) {
    int n = names.length;
    String queryText = prepareQueryToListTablesWithSimilarNames(n);
    String[] tableCatNames = query(queryText, RowsCollectors.array(String.class), (Object[])names);
    if (tableCatNames.length == 0) return;

    dropTables(tableCatNames);
  }


  protected abstract String prepareQueryToListTablesWithSimilarNames(int n);


  public void dropTables(final String... tableCatNames) {
    facade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {

        for (String tableCatName : tableCatNames) {
          String tableScrName = nameCatalogToScript(tableCatName);
          String template = dropTableTemplate();
          String cmd = replace(template, "TABLE", tableScrName);
          session.command(cmd).run();
        }

      }
    });
  }

  @NotNull
  protected String dropTableTemplate() {
    return "drop table TABLE";
  }


}

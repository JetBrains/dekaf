package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.intermediate.AdaptIntermediateRdbmsProvider;
import org.jetbrains.jdba.intermediate.IntegralIntermediateFacade;
import org.jetbrains.jdba.jdbc.BaseHyperSonicCase;
import org.jetbrains.jdba.jdbc.UnknownDatabaseProvider;
import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseSessionTest extends BaseHyperSonicCase {


  @Test
  public void basic_scenario() {
    AdaptIntermediateRdbmsProvider interProvider =
            new AdaptIntermediateRdbmsProvider(UnknownDatabaseProvider.INSTANCE);
    IntegralIntermediateFacade interFacade =
            interProvider.openFacade(HSQL_CONNECTION_STRING, null, 1);

    BaseFacade facade = new BaseFacade(interFacade);
    facade.connect();

    facade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        session.command("create table tab1 (x integer)").run();

      }
    });

    facade.inTransaction(new InTransactionNoResult() {
      @Override
      public void run(@NotNull final DBTransaction tran) {

        tran.command("insert into tab1 values(44)").run();

      }
    });

    final SqlQuery<Integer> query =
            new SqlQuery<Integer>("select * from tab1", Layouts.singleOf(Integer.class));
    int x = facade.inTransaction(new InTransaction<Integer>() {
      @Override
      public Integer run(@NotNull final DBTransaction tran) {

        return tran.query(query).run();

      }
    });

    facade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        session.command("drop table tab1").run();

      }
    });

    facade.disconnect();

    assertThat(x).isEqualTo(44);
  }


}
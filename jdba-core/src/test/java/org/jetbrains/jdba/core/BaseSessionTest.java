package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.intermediate.IntegralIntermediateSession;
import org.jetbrains.jdba.jdbc.JdbcIntermediateSession;
import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.core.Layouts.columnOf;
import static org.jetbrains.jdba.core.Layouts.singleOf;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
public class BaseSessionTest extends BaseHyperSonicFacadeCase {


  @Test
  public void basic_scenario() {
    myFacade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        session.command("create table tab1 (x integer)").run();

        session.inTransaction(new InTransactionNoResult() {
          @Override
          public void run(@NotNull final DBTransaction tran) {

            tran.command("insert into tab1 values(44)").run();

          }
        });

        final SqlQuery<Integer> query =
                new SqlQuery<Integer>("select * from tab1", singleOf(Integer.class));
        int x = session.inTransaction(new InTransaction<Integer>() {
          @Override
          public Integer run(@NotNull final DBTransaction tran) {

            return tran.query(query).run();

          }
        });

        session.command("drop table tab1").run();

        assertThat(x).isEqualTo(44);

      }
    });

    checkAllAreClosed();
  }


  @Test
  public void iterative_scenario() {
    myFacade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        session.command("create table Turbo2000 (x smallint)").run();

        DBCommandRunner cr = session.command("insert into Turbo2000 values (?)");
        for (short i = 1; i <= 2000; i++)
          cr.withParams(i).run();

        DBQueryRunner<Short[]> qr =
                session.query("select * from Turbo2000 order by 1", columnOf(Short.class))
                       .packBy(100);
        Short[] pack = qr.run();

        assertThat(pack).isNotNull()
                        .startsWith((short) 1)
                        .endsWith((short) 100);

        int packs = 0;
        while (pack != null) {
          packs++;
          pack = qr.nextPack();
        }

        assertThat(packs).isEqualTo(20); // 2000 / 100 = 20
      }
    });

    checkAllAreClosed();
  }


  @Test
  public void transaction_should_close_seances() {
    myFacade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        session.command("create table just_table_333 (x integer)").run();

        session.inTransaction(new InTransactionNoResult() {
          @Override
          public void run(@NotNull final DBTransaction tran) {

            tran.command("insert into just_table_333 values (111), (222), (333)").run();
            tran.query("select * from just_table_333", singleOf(Integer.class))
                .packBy(1)
                .run();

          }
        });

        JdbcIntermediateSession intermediateSession =
            session.getSpecificService(JdbcIntermediateSession.class,
                                       ImplementationAccessibleService.Names.INTERMEDIATE_SERVICE);
        assert intermediateSession != null;
        assertThat(intermediateSession.countOpenedSeances()).isZero();
        assertThat(intermediateSession.countOpenedCursors()).isZero();

      }
    });
  }


  @Test
  public void get_intermediate_service() {
    myFacade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        final IntegralIntermediateSession intermediateSession =
            session.getSpecificService(
                IntegralIntermediateSession.class,
                ImplementationAccessibleService.Names.INTERMEDIATE_SERVICE);
        assertThat(intermediateSession).isInstanceOf(JdbcIntermediateSession.class);

      }
    });
  }

  @Test
  public void get_jdbc_connection() {
    myFacade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        java.sql.Connection connection =
            session.getSpecificService(java.sql.Connection.class,
                                       ImplementationAccessibleService.Names.JDBC_CONNECTION);
        assertThat(connection).isNotNull()
                              .isInstanceOf(java.sql.Connection.class);

      }
    });
  }


}
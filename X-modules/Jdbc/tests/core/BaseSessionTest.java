package org.jetbrains.dekaf.core;

import org.assertj.core.api.Assertions;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateSession;
import org.jetbrains.dekaf.jdbc.JdbcIntermediateSession;
import org.jetbrains.dekaf.sql.SqlQuery;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.core.Layouts.columnOf;
import static org.jetbrains.dekaf.core.Layouts.singleOf;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
public class BaseSessionTest extends BaseInMemoryDBFacadeCase {


  @Test
  public void basic_scenario() {
    myFacade.inSessionDo(session -> {

        session.command("create table tab1 (x integer)").run();

        session.inTransactionDo(tran -> {

            tran.command("insert into tab1 values(44)").run();

        });

        final SqlQuery<Integer> query =
                new SqlQuery<Integer>("select * from tab1", singleOf(Integer.class));
        int x = session.inTransaction(tran -> {

            return tran.query(query).run();

        });

        session.command("drop table tab1").run();

        Assertions.assertThat(x).isEqualTo(44);

    });

    checkAllAreClosed();
  }


  @Test
  public void iterative_scenario() {
    myFacade.inSessionDo(session -> {

        session.command("create table Turbo2000 (x smallint)").run();

        DBCommandRunner cr = session.command("insert into Turbo2000 values (?)");
        for (short i = 1; i <= 2000; i++)
          cr.withParams(i).run();

        DBQueryRunner<Short[]> qr =
                session.query("select * from Turbo2000 order by 1", columnOf(Short.class))
                       .packBy(100);
        Short[] pack = qr.run();

        Assertions.assertThat(pack).isNotNull()
                  .startsWith((short) 1)
                  .endsWith((short) 100);

        int packs = 0;
        while (pack != null) {
          packs++;
          pack = qr.nextPack();
        }

        Assertions.assertThat(packs).isEqualTo(20); // 2000 / 100 = 20
    });

    checkAllAreClosed();
  }


  @Test
  public void isInTransaction_begin_commit() {
    final DBLeasedSession session = myFacade.leaseSession();
    try {
      Assertions.assertThat(session.isInTransaction()).isFalse();

      session.beginTransaction();

      Assertions.assertThat(session.isInTransaction()).isTrue();

      session.commit();

      Assertions.assertThat(session.isInTransaction()).isFalse();
    }
    finally {
      session.close();
    }
  }

  @Test
  public void isInTransaction_begin_rollback() {
    final DBLeasedSession session = myFacade.leaseSession();
    try {
      Assertions.assertThat(session.isInTransaction()).isFalse();

      session.beginTransaction();

      Assertions.assertThat(session.isInTransaction()).isTrue();

      session.rollback();

      Assertions.assertThat(session.isInTransaction()).isFalse();
    }
    finally {
      session.close();
    }
  }


  @Test
  public void transaction_should_close_seances() {
    myFacade.inSessionDo(session -> {

        session.command("create table just_table_333 (x integer)").run();

        session.inTransactionDo(tran -> {

            tran.command("insert into just_table_333 values (111), (222), (333)").run();
            tran.query("select * from just_table_333", singleOf(Integer.class))
                .packBy(1)
                .run();

        });

        JdbcIntermediateSession intermediateSession =
            session.getSpecificService(JdbcIntermediateSession.class,
                                       ImplementationAccessibleService.Names.INTERMEDIATE_SERVICE);
        assert intermediateSession != null;
        assertThat(intermediateSession.countOpenedSeances()).isZero();
        assertThat(intermediateSession.countOpenedCursors()).isZero();

    });
  }


  @Test
  public void get_intermediate_service() {
    myFacade.inSessionDo(session -> {

        final IntegralIntermediateSession intermediateSession =
            session.getSpecificService(
                IntegralIntermediateSession.class,
                ImplementationAccessibleService.Names.INTERMEDIATE_SERVICE);
        Assertions.assertThat(intermediateSession).isInstanceOf(JdbcIntermediateSession.class);

    });
  }

  @Test
  public void get_jdbc_connection() {
    myFacade.inSessionDo(session -> {

        java.sql.Connection connection =
            session.getSpecificService(java.sql.Connection.class,
                                       ImplementationAccessibleService.Names.JDBC_CONNECTION);
        Assertions.assertThat(connection).isNotNull()
                  .isInstanceOf(java.sql.Connection.class);

    });
  }


}
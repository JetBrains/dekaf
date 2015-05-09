package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.jdbc.BaseHyperSonicCase;
import org.jetbrains.jdba.jdbc.JdbcIntermediateFacade;
import org.jetbrains.jdba.jdbc.UnknownDatabaseProvider;
import org.jetbrains.jdba.sql.SqlQuery;
import org.junit.*;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
public class BaseSessionTest extends BaseHyperSonicCase {

  private static JdbcIntermediateFacade ourInterFacade;

  private static BaseFacade ourFacade;


  @BeforeClass
  public static void setup() {
    ourInterFacade = UnknownDatabaseProvider.INSTANCE.openFacade(HSQL_CONNECTION_STRING, null, 1);
    ourFacade = new BaseFacade(ourInterFacade);
  }

  @Before
  public void connect() {
    ourFacade.connect();
  }

  @After
  public void disconnect() {
    ourFacade.disconnect();
  }

  @Test
  public void basic_scenario() {
    ourFacade.inSession(new InSessionNoResult() {
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
                new SqlQuery<Integer>("select * from tab1", Layouts.singleOf(Integer.class));
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
    ourFacade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        session.command("create table Turbo2000 (x smallint)").run();

        DBCommandRunner cr = session.command("insert into Turbo2000 values (?)");
        for (short i = 1; i <= 2000; i++)
          cr.withParams(i).run();

        DBQueryRunner<Short[]> qr =
                session.query("select * from Turbo2000 order by 1", Layouts.columnOf(Short.class))
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


  private void checkAllAreClosed() {
    assertThat(ourInterFacade.countOpenedCursors()).isZero();
    assertThat(ourInterFacade.countOpenedSeances()).isZero();
    assertThat(ourInterFacade.countOpenedSessions()).isZero();
  }

}
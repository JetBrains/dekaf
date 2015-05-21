package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.Scriptum;
import org.jetbrains.jdba.sql.SqlQuery;
import org.jetbrains.jdba.util.ArrayFunctions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jetbrains.jdba.core.Layouts.listOf;
import static org.jetbrains.jdba.core.Layouts.oneOf;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public abstract class BaseTestHelper<F extends DBFacade> implements DBTestHelper {

  @NotNull
  protected final F facade;

  @NotNull
  protected final Scriptum scriptum;


  protected BaseTestHelper(@NotNull final F facade, final @NotNull Scriptum scriptum) {
    this.facade = facade;
    this.scriptum = scriptum;

    myGenDeleteTablesOrViews = scriptum.query("GenDeleteTablesOrViews", listOf(oneOf(String.class)));
  }



  @Override
  public void ensureNoTableOrView(final String... names) {
    final String[][] namePacks = ArrayFunctions.splitArrayPer(names, 4);
    final List<String> commands = new ArrayList<String>(names.length);

    facade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        DBQueryRunner<List<String>> queryRunner = session.query(myGenDeleteTablesOrViews);

        for (String[] namePack : namePacks) {
          String[] params;
          if (namePack.length == 4) {
            params = namePack;
          }
          else {
            params = new String[4];
            System.arraycopy(namePack, 0, params, 0, namePack.length);
            Arrays.fill(params, namePack.length, 4, "");
          }

          List<String> cmds = queryRunner.withParams(params).run();
          commands.addAll(cmds);
        }

        //SqlScript dropScript = new SqlScript(commands.toArray(new String[commands.size()]));
        // session.script(dropScript).run();

        for (String command : commands) {
          session.command(command).run();
        }
      }
    });
  }

  private final SqlQuery<List<String>> myGenDeleteTablesOrViews;

}

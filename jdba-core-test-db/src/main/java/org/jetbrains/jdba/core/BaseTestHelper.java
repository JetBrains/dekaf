package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.*;
import org.jetbrains.jdba.util.Collects;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
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
  }


  @Override
  public void performCommand(@NotNull final Scriptum scriptum, @NotNull final String commandName) {
    final SqlCommand command = scriptum.command(commandName);
    performCommand(command);
  }

  @Override
  public void performCommand(@NotNull final String commandText) {
    final SqlCommand command = new SqlCommand(commandText);
    performCommand(command);
  }

  @Override
  public void performCommand(@NotNull final SqlCommand command) {
    facade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {
        session.command(command).run();
      }
    });
  }

  @Override
  public void performScript(@NotNull final SqlScript script) {
    facade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {
        session.script(script).run();
      }
    });
  }

  @Override
  public void performScript(final String... commands) {
    if (commands == null || commands.length == 0) return;

    SqlScriptBuilder b = new SqlScriptBuilder();
    for (String c : commands) b.add(c);

    performScript(b.build());
  }

  @Override
  public void performCommandOrMetaQueryCommands(@NotNull final Scriptum scriptum,
                                                @NotNull final String operationName) {
    if (scriptum.findText(operationName + "Command") != null) {
      performCommand(scriptum, operationName + "Command");
    }
    else if (scriptum.findText(operationName + "MetaQuery") != null) {
      performMetaQueryCommands(scriptum, operationName + "MetaQuery");
    }
    else {
      throw new IllegalArgumentException(format(
          "The scriptum has no operation %sCommand or %sMetaQuery",
          operationName, operationName));
    }
  }


  @Override
  public void performMetaQueryCommands(@NotNull final Scriptum scriptum,
                                       @NotNull final String metaQueryName,
                                       final Object... params) {
    final SqlQuery<List<String>> metaQuery =
        scriptum.query(metaQueryName, listOf(oneOf(String.class)));

    facade.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        List<String> commands = session.query(metaQuery).withParams(params).run();

        SqlScriptBuilder sb = new SqlScriptBuilder();
        for (String command : commands)
          if (command != null && command.length() > 0)
            sb.add(command);

        SqlScript script = sb.build();

        session.script(script).run();

      }
    });
  }



  @Override
  public void ensureNoTableOrView(final String... names) {
    final String[][] namePacks = Collects.splitArrayPer(names, 4);
    for (String[] namePack : namePacks) {
      Object[] params;
      if (namePack.length == 4) {
        params = namePack;
      }
      else {
        params = new String[4];
        System.arraycopy(namePack, 0, params, 0, namePack.length);
        Arrays.fill(params, namePack.length, 4, "");
      }

      performMetaQueryCommands(scriptum, "EnsureNoTableOrViewMetaQuery", params);
    }
  }


  @Override
  public void zapSchema() {
    performCommandOrMetaQueryCommands(scriptum, "ZapSchema");
  }



}

package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.exceptions.DBProtectionException;
import org.jetbrains.jdba.exceptions.NoTableOrViewException;
import org.jetbrains.jdba.sql.*;
import org.jetbrains.jdba.util.Collects;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.String.format;
import static org.jetbrains.jdba.core.Layouts.*;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public abstract class BaseTestHelper<F extends DBFacade> implements DBTestHelper {

  @NotNull
  protected final F db;

  @NotNull
  protected final Scriptum scriptum;

  @NotNull
  protected final Set<String> schemasNotToZap =
      new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);



  protected BaseTestHelper(@NotNull final F db, final @NotNull Scriptum scriptum) {
    this.db = db;
    this.scriptum = scriptum;

    schemasNotToZap.add("information_schema");
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
    db.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {
        session.command(command).run();
      }
    });
  }

  @Override
  public void performCommand(@NotNull final DBTransaction transaction,
                             @NotNull final Scriptum scriptum, @NotNull final String commandName) {
    final SqlCommand command = scriptum.command(commandName);
    performCommand(transaction, command);
  }

  @Override
  public void performCommand(@NotNull final DBTransaction transaction,
                             @NotNull final String commandText) {
    final SqlCommand command = new SqlCommand(commandText);
    performCommand(transaction, command);
  }

  @Override
  public void performCommand(@NotNull final DBTransaction transaction,
                             @NotNull final SqlCommand command) {
    transaction.command(command).run();
  }

  @Override
  public void performScript(@NotNull final Scriptum scriptum, @NotNull final String scriptName) {
    final SqlScript script = scriptum.script(scriptName);
    performScript(script);
  }

  @Override
  public void performScript(@NotNull final SqlScript script) {
    db.inSession(new InSessionNoResult() {
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

    db.inSession(new InSessionNoResult() {
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
  public int countTableRows(@NotNull final String tableName) {
    return db.inSession(new InSession<Integer>() {
      @Override
      public Integer run(@NotNull final DBSession session) {

        return countTableRows(session, tableName);

      }
    });
  }

  @Override
  public int countTableRows(@NotNull final DBTransaction transaction, @NotNull final String tableName) {
    final String queryText = "select count(*) from " + tableName;

    try {
      return transaction.query(queryText, singleOf(Integer.class)).run();
    }
    catch (NoTableOrViewException ntv) {
      return Integer.MIN_VALUE;
    }
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
    final ConnectionInfo connectionInfo = db.getConnectionInfo();
    final String schemaName = connectionInfo.schemaName;
    if (schemaName != null && schemasNotToZap.contains(schemaName))
      throw new DBProtectionException(format("The schema %s must not be zapped", schemaName), "zapSchema");

    zapSchemaInternally(connectionInfo);
  }

  protected void zapSchemaInternally(final ConnectionInfo connectionInfo) {
    performCommandOrMetaQueryCommands(scriptum, "ZapSchema");
  }


}

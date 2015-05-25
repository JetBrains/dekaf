package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.SqlCommand;
import org.jetbrains.jdba.sql.SqlQuery;
import org.jetbrains.jdba.sql.SqlScript;
import org.jetbrains.jdba.sql.SqlStatement;

import java.util.List;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class BaseScriptRunner implements DBScriptRunner {

  @NotNull
  private final DBTransaction myTransaction;

  @NotNull
  private final SqlScript myScript;


  BaseScriptRunner(@NotNull final DBTransaction transaction,
                   @NotNull final SqlScript script) {
    myTransaction = transaction;
    myScript = script;
  }


  @Override
  public DBScriptRunner run() {
    List<? extends SqlStatement> statements = myScript.getStatements();

    for (SqlStatement statement : statements) {
      if (statement instanceof SqlCommand) {
        runCommand((SqlCommand)statement);
      }
      if (statement instanceof SqlQuery) {
        runQuery((SqlQuery) statement);
      }
    }

    return this;
  }

  private void runCommand(final SqlCommand command) {
    myTransaction.command(command).run();
  }

  private void runQuery(final SqlQuery query) {
    myTransaction.query(query).run();
  }


}

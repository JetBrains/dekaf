package org.jetbrains.dba.fakedb.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.core.DBCommandRunner;
import org.jetbrains.dba.core.DBQueryRunner;
import org.jetbrains.dba.core.DBScriptRunner;
import org.jetbrains.dba.core.DBTransaction;
import org.jetbrains.dba.sql.SQLCommand;
import org.jetbrains.dba.sql.SQLQuery;
import org.jetbrains.dba.sql.SQLScript;



/**
 * @author Leonid Bushuev from JetBrains
 */
class FakeTransaction implements DBTransaction {

  @NotNull
  final FakeSession session;


  FakeTransaction(@NotNull final FakeSession session) {
    this.session = session;
  }


  @Override
  public DBCommandRunner command(@NotNull SQLCommand command) {
    // TODO implement FakeTransaction.command()
    throw new RuntimeException("Method FakeTransaction.command() is not implemented yet.");
  }


  @Override
  public DBCommandRunner command(@NotNull String commandText) {
    // TODO implement FakeTransaction.command()
    throw new RuntimeException("Method FakeTransaction.command() is not implemented yet.");
  }


  @Override
  public <S> DBQueryRunner<S> query(@NotNull SQLQuery<S> query) {
    // TODO implement FakeTransaction.query()
    throw new RuntimeException("Method FakeTransaction.query() is not implemented yet.");
  }


  @Override
  public DBScriptRunner script(@NotNull SQLScript script) {
    // TODO implement FakeTransaction.script()
    throw new RuntimeException("Method FakeTransaction.script() is not implemented yet.");
  }
}

package org.jetbrains.dba.sql;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;



/**
 * A builder for SQL script.
 * @author Leonid Bushuev from JetBrains
 */
public class SQLScriptBuilder {

  private final ImmutableList.Builder<SQLCommand> myCommands =
    new ImmutableList.Builder<SQLCommand>();

  @NotNull
  private final SQL sql;


  SQLScriptBuilder(@NotNull final SQL sql) {
    this.sql = sql;
  }


  public void add(@NotNull String... commands) {
    for (String command : commands) {
      SQLCommand sqlCommand = sql.command(command);
      myCommands.add(sqlCommand);
    }
  }

  public void add(@NotNull SQLCommand... commands) {
    for (SQLCommand command : commands) {
      myCommands.add(command);
    }
  }

  public void add(@NotNull SQLScript... scripts) {
    for (SQLScript script : scripts) {
      for (SQLCommand command : script.myCommands) {
        myCommands.add(command);
      }
    }
  }


  @NotNull
  public SQLScript build() {
    return sql.instantiateSQLScript(myCommands.build());
  }


}

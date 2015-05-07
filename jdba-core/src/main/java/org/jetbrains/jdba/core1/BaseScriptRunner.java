package org.jetbrains.jdba.core1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.DBScriptRunner;
import org.jetbrains.jdba.sql.SqlCommand;
import org.jetbrains.jdba.sql.SqlScript;

import java.util.ArrayList;
import java.util.List;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseScriptRunner implements DBScriptRunner {

  @NotNull
  protected final BaseSession mySession;

  @NotNull
  protected final SqlScript myScript;

  @NotNull
  protected final List<BaseCommandRunner> myCommandRunners;



  public BaseScriptRunner(@NotNull final BaseSession session,
                          @NotNull final SqlScript script) {
    mySession = session;
    myScript = script;

    List<SqlCommand> commands = script.getCommands();
    myCommandRunners = new ArrayList<BaseCommandRunner>(commands.size());

    for (SqlCommand command : commands) {
      final BaseCommandRunner runner = mySession.command(command);
      myCommandRunners.add(runner);
    }
  }


  @Override
  public BaseScriptRunner run() {
    for (BaseCommandRunner runner : myCommandRunners) {
      runner.run();
    }
    return this;
  }
}

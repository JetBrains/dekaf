package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.sql.SQLCommand;
import org.jetbrains.dba.sql.SQLScript;

import java.util.ArrayList;
import java.util.List;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseScriptRunner implements DBScriptRunner {

  @NotNull
  protected final BaseSession mySession;

  @NotNull
  protected final SQLScript myScript;

  @NotNull
  protected final List<BaseCommandRunner> myCommandRunners;



  public BaseScriptRunner(@NotNull final BaseSession session,
                          @NotNull final SQLScript script) {
    mySession = session;
    myScript = script;

    List<SQLCommand> commands = script.getCommands();
    myCommandRunners = new ArrayList<BaseCommandRunner>(commands.size());

    for (SQLCommand command : commands) {
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

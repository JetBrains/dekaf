package org.jetbrains.jdba.oracle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.DBFacade;
import org.jetbrains.jdba.core.DBSession;
import org.jetbrains.jdba.core.InSessionNoResult;
import org.jetbrains.jdba.sql.SQLCommand;
import org.jetbrains.jdba.sql.Scriptum;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class OracleTestHelper {

  private final Scriptum myScriptum = Scriptum.of(OracleTestHelper.class);

  @NotNull
  private DBFacade myFacade;


  OracleTestHelper(@NotNull final DBFacade facade) {
    myFacade = facade;
  }

  public void cleanupDatabase() {
    final SQLCommand zapSchemaCommand = myScriptum.command("ZapSchema");
    myFacade.inSession(new InSessionNoResult() {
      public void run(@NotNull final DBSession session) {
        session.command(zapSchemaCommand).run();
      }
    });
  }



}

package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.CommonIntegrationCase;
import org.jetbrains.dekaf.sql.SqlCommand;
import org.junit.Before;
import org.junit.Test;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class CommonCommandRunnerTest extends CommonIntegrationCase {

  @Before
  public void setUp() throws Exception {
    DB.connect();
  }


  @Test
  public void basic_create_drop() {
    TH.ensureNoTableOrView("Tab1");

    final SqlCommand command1 = new SqlCommand("create table Tab1 (ColA char(1))");
    final SqlCommand command2 = new SqlCommand("drop table Tab1");

    DB.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        session.command(command1).run();
        session.command(command2).run();

      }
    });
  }




}

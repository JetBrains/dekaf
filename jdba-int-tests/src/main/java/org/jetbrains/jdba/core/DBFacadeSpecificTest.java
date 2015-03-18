package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import testing.categories.ForMSSQL;

import static org.jetbrains.jdba.TestDB2.FACADE;



@FixMethodOrder(MethodSorters.JVM)
public class DBFacadeSpecificTest {


  @Test @Category(ForMSSQL.class)
  public void setLanguage() {
    assert FACADE.isConnected();
    FACADE.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull DBSession session) {
        session.command("set language us_english").run();
      }
    });
  }


}
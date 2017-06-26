package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.text.Scriptum;

import java.util.Arrays;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class OracleTestHelper extends BaseTestHelper<DBFacade> {

  public OracleTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(OracleTestHelper.class));
    schemasNotToZap.clear();
    //noinspection SpellCheckingInspection
    schemasNotToZap.addAll(Arrays.asList("SYS", "SYSTEM", "SYSMAN", "PUBLIC", "CTXSYS", "DBSNMP",
                                         "APPQOSSYS", "EXFSYS", "ORACLE_OCM", "OUTLN", "WMSYS",
                                         "XDB", "BENCHMARK"));
  }

  @NotNull
  @Override
  public String fromSingleRowTable() {
    return " from dual";
  }

  @Override
  public void prepareX1() {
    performCommand("create or replace view X1 as select 1 as X from dual");
  }

  @Override
  public void prepareX1000() {
    performCommand(scriptum, "X1000");
  }

  @Override
  public void prepareX1000000() {
    prepareX1000();
    performCommand(scriptum, "X1000000");
  }
}

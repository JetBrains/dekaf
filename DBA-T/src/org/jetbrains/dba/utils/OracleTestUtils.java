package org.jetbrains.dba.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.access.DBFacade;
import org.jetbrains.dba.access.OraFacade;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class OracleTestUtils extends BaseTestUtils {


  public OracleTestUtils(@NotNull DBFacade facade) {
    super(facade);
    assert facade instanceof OraFacade;
  }


  @Override
  public boolean nameIsSafe(@NotNull final String name) {
    for (int i = 0, n = name.length(); i < n; i++) {
      final char c = name.charAt(i);
      if (Character.isUpperCase(c)) continue;
      if (i == 0) return false;
      if (Character.isDigit(c)) continue;
      if (c == '_' || c == '$') continue;
      return false;
    }
    return true;
  }


  @Override
  String prepareQueryToListTablesWithSimilarNames(int n) {
    return "select table_name from tabs where upper(table_name) in (" + Strings.repeat("upper(?)", ",", n) + ")";
  }


  @NotNull
  @Override
  protected String dropTableTemplate() {
    return "drop table TABLE cascade constraints";
  }
}

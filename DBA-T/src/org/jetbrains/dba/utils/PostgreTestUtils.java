package org.jetbrains.dba.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.core.DBFacade;
import org.jetbrains.dba.rdbms.postgre.PostgreFacade;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class PostgreTestUtils extends BaseTestUtils {


  public PostgreTestUtils(@NotNull DBFacade facade) {
    super(facade);
    assert facade instanceof PostgreFacade;
  }


  @Override
  public boolean nameIsSafe(@NotNull final String name) {
    for (int i = 0, n = name.length(); i < n; i++) {
      final char c = name.charAt(i);
      if (Character.isLowerCase(c)) continue;
      if (i == 0) return false;
      if (Character.isDigit(c)) continue;
      if (c == '_') continue;
      return false;
    }
    return true;
  }


  @Override
  String prepareQueryToListTablesWithSimilarNames(int n) {
    return "select table_name from information_schema.tables where lower(table_name) in (" + Strings.repeat("lower(?)", ",", n) + ")";
  }


}

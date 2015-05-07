package org.jetbrains.jdba.mysql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core1.DBFacade;
import org.jetbrains.jdba.util.BaseTestUtils;
import org.jetbrains.jdba.util.Strings;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class MysqlTestUtils extends BaseTestUtils {


  public MysqlTestUtils(@NotNull DBFacade facade) {
    super(facade);
    assert facade instanceof MysqlFacade;
  }


  @Override
  public boolean nameIsSafe(@NotNull final String name) {
    // TODO reimplement based on current database settings
    for (int i = 0, n = name.length(); i < n; i++) {
      final char c = name.charAt(i);
      if ('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z') continue;
      if (i == 0) return false;
      if ('0' <= c && c <= '9' || c == '_') continue;
      return false;
    }
    return true;
  }


  @Override
  protected String prepareQueryToListTablesWithSimilarNames(int n) {
    return "select table_name from information_schema.tables where table_schema = schema() and lower(table_name) in (" + Strings.repeat("lower(?)",
                                                                                                                                        ",",
                                                                                                                                        n) + ")";
  }
}

package org.jetbrains.jdba.microsoft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.DBFacade;
import org.jetbrains.jdba.util.BaseTestUtils;
import org.jetbrains.jdba.util.Strings;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class MicrosoftTestUtils extends BaseTestUtils {


  public MicrosoftTestUtils(@NotNull DBFacade facade) {
    super(facade);
    assert facade instanceof MssqlFacade;
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


  @NotNull
  @Override
  public String nameCatalogToScript(@NotNull String nameFromCatalog) {
    return nameIsSafe(nameFromCatalog) ? nameFromCatalog : '[' + nameFromCatalog + ']';
  }


  @Override
  protected String prepareQueryToListTablesWithSimilarNames(int n) {
    return "select TABLE_NAME " +
           "from INFORMATION_SCHEMA.TABLES " +
           "where TABLE_SCHEMA = schema_name() " +
           "and lower(table_name) in (" + Strings.repeat("lower(?)", ",", n) + ")";
  }
}

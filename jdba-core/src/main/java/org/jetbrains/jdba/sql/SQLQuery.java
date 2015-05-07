package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core1.DBRowsCollector;



/**
 * A SQL query that returns a list of rows.
 *
 * @param <S> type of the query result.
 * @author Leonid Bushuev from JetBrains
 */
public class SQLQuery<S> extends SQLExecutable {


  @NotNull
  final DBRowsCollector<S> collector;


  private transient String displayName;


  public SQLQuery(@NotNull final String sourceText,
                  @NotNull final DBRowsCollector<S> collector) {
    super(sourceText);
    this.collector = collector;
  }


  public String getDisplayName() {
    if (displayName == null) {
      prepareDisplayName();
    }
    return displayName;
  }


  private synchronized void prepareDisplayName() {
    if (displayName != null) return;

    int nl = mySourceText.indexOf('\n');
    String str1 = nl > 0 ? mySourceText.substring(0, nl) : mySourceText;
    str1 = str1.trim();

    displayName = str1;
  }


  @NotNull
  public String getSourceText() {
    return mySourceText;
  }


  @NotNull
  public DBRowsCollector<S> getCollector() {
    return collector;
  }


  @Override
  public String toString() {
    return getDisplayName();
  }
}

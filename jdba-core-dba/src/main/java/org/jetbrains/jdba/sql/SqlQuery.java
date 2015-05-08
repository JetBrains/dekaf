package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.ResultLayout;



/**
 * A SQL query that returns a list of rows.
 *
 * @param <S> type of the query result.
 * @author Leonid Bushuev from JetBrains
 */
public class SqlQuery<S> extends SqlExecutable {


  @NotNull
  private final ResultLayout<S> myLayout;


  private transient String displayName;


  public SqlQuery(@NotNull final String sourceText,
           @NotNull final ResultLayout<S> layout) {
    super(sourceText);
    this.myLayout = layout;
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
  public ResultLayout<S> getLayout() {
    return myLayout;
  }

  @Override
  public String toString() {
    return getDisplayName();
  }
}

package org.jetbrains.dba.sql;

/**
 * MSSQL' SQL.
 * @author Leonid Bushuev from JetBrains
 */
public class MicrosoftSQL extends SQL {


  private boolean myCaseInsensitive;


  public boolean isCaseInsensitive() {
    return myCaseInsensitive;
  }


  public void setCaseInsensitive(boolean caseInsensitive) {
    myCaseInsensitive = caseInsensitive;
  }


  @Override
  protected MicrosoftSQL clone() {
    MicrosoftSQL clone = (MicrosoftSQL) super.clone();
    clone.myCaseInsensitive = this.myCaseInsensitive;
    return clone;
  }
}

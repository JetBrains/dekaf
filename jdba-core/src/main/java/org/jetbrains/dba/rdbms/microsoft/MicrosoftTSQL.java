package org.jetbrains.dba.rdbms.microsoft;

import org.jetbrains.dba.sql.SQL;



/**
 * MSSQL' SQL.
 * @author Leonid Bushuev from JetBrains
 */
public class MicrosoftTSQL extends SQL {


  private boolean myCaseInsensitive;


  public boolean isCaseInsensitive() {
    return myCaseInsensitive;
  }


  public void setCaseInsensitive(boolean caseInsensitive) {
    myCaseInsensitive = caseInsensitive;
  }


  @Override
  public MicrosoftTSQL clone() {
    MicrosoftTSQL clone = (MicrosoftTSQL) super.clone();
    clone.myCaseInsensitive = this.myCaseInsensitive;
    return clone;
  }
}

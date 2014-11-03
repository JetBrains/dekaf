package org.jetbrains.dba.sql;

/**
 * Oracle SQL.
 * @author Leonid Bushuev from JetBrains
 */
public class OraSQL extends SQL {


  @Override
  public OraSQL clone() {
    return (OraSQL) super.clone();
  }
}

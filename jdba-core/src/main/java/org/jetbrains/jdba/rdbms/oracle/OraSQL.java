package org.jetbrains.jdba.rdbms.oracle;

import org.jetbrains.jdba.sql.SQL;



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

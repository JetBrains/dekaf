package org.jetbrains.dba.access;

import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.TestDB;
import org.jetbrains.dba.sql.OraSQL;
import org.jetbrains.dba.sql.SQL;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class DBTestCase {


  protected final Rdbms myRdbms;
  protected final DBFacade myFacade;
  protected final SQL sql;


  protected DBTestCase() {
    myRdbms = TestDB.PRIMARY_RDBMS;
    myFacade = TestDB.provide();
    myFacade.connect();
    sql = new OraSQL(); // TODO use an appropriate factory
  }


}

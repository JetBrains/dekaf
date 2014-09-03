package org.jetbrains.dba;

import org.jetbrains.dba.access.DBFacade;
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
    myRdbms = TestDB.ourRdbms;
    myFacade = TestDB.ourDB;
    myFacade.connect();
    sql = new OraSQL(); // TODO use an appropriate factory
  }



}

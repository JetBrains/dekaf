package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.access.DBFacade;
import org.jetbrains.dba.access.DBSession;
import org.jetbrains.dba.access.InSessionNoResult;
import org.jetbrains.dba.sql.OraSQL;
import org.jetbrains.dba.sql.SQL;
import org.jetbrains.dba.sql.SQLCommand;



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

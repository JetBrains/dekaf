package org.jetbrains.dba.fakedb;

import org.jetbrains.dba.Rdbms;



/**
 * Fake DB is designed for testing purposes.
 *
 * @author Leonid Bushuev from JetBrains
 */
public final class FakeDB {

  public final static Rdbms RDBMS = new Rdbms("FakeDB");

}

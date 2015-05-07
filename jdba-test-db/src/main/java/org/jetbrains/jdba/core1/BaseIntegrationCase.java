package org.jetbrains.jdba.core1;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class BaseIntegrationCase {


  protected static BaseTestDB db;


  public static void connectToDB() {
    db = BaseTestDB.connect();
  }





}

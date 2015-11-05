package org.jetbrains.jdba.jdbc;

/**
 * @author Leonid Bushuev from JetBrains
 */
public final class UnknownDatabaseExceptionRecognizer extends BaseExceptionRecognizer {

  public final static UnknownDatabaseExceptionRecognizer INSTANCE = new UnknownDatabaseExceptionRecognizer();

}

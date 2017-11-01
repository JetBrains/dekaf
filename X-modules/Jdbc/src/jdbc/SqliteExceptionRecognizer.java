package org.jetbrains.dekaf.jdbc;

/**
 * @author Leonid Bushuev from JetBrains
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class SqliteExceptionRecognizer extends BaseExceptionRecognizer {

  public static final SqliteExceptionRecognizer INSTANCE = new SqliteExceptionRecognizer();

}

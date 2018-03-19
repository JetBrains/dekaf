package org.jetbrains.dekaf;

/**
 * Unknown/unspecified JDBC-accessible RDBMS declaration class.
 *
 * @author Leonid Bushuev
 */
public abstract class GenericJdbcDatabase {

    /**
     * Unknown RDBMS marker.
     */
    public static final Rdbms RDBMS = Rdbms.of("UNKNOWN");

}

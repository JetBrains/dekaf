package org.jetbrains.dekaf.jdbc;


import org.jetbrains.annotations.NotNull;



/**
 * JDBC entry points.
 */
class JdbcMaster {

    @NotNull
    static final JdbcDriverManager ourDriverManager = new JdbcDriverManager();

}

package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;



public class JdbcDriverLoadingException extends JdbcDriverException {

    public JdbcDriverLoadingException(final @NotNull String message)
    {
        super(message, "Loading JDBC driver");
    }

    public JdbcDriverLoadingException(final @NotNull SQLException sqlException)
    {
        super(sqlException, "Loading JDBC driver");
    }

    public JdbcDriverLoadingException(final @NotNull String message,
                                      final @NotNull Exception exception)
    {
        super(message, exception, "Loading JDBC driver");
    }

}

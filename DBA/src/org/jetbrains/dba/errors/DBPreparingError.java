package org.jetbrains.dba.errors;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;


/**
 * Throws when a problem of a SQL command or query initialization error occured.
 * @author Leonid Bushuev from JetBrains
 */
public class DBPreparingError extends DBError
{
    public DBPreparingError(@NotNull final SQLException sqlException)
    {
        super(sqlException);
    }


    public DBPreparingError(@NotNull final String message, @NotNull final SQLException sqlException)
    {
        super(message, sqlException);
    }


    public DBPreparingError(@NotNull final String message, @NotNull final Exception exception)
    {
        super(message, exception);
    }


    public DBPreparingError(@NotNull final String message)
    {
        super(message);
    }
}

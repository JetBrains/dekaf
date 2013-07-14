package org.jetbrains.dba.errors;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;


/**
 * Abstract database access error
 **/
public abstract class DBError extends RuntimeException
{
    public final int vendorErrorCode;



    protected DBError(@NotNull final SQLException sqlException)
    {
        this(sqlException.getMessage(), sqlException);
    }

    protected DBError(@NotNull final String message, @NotNull final SQLException sqlException)
    {
        super(message, sqlException);
        this.vendorErrorCode = sqlException.getErrorCode();
    }

    protected DBError(@NotNull final String message, @NotNull final Exception exception)
    {
        super(message, exception);
        this.vendorErrorCode = 0;
    }

    protected DBError(@NotNull final String message)
    {
        super(message);
        this.vendorErrorCode = 0;
    }

}

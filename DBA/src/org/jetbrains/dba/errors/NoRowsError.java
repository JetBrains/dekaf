package org.jetbrains.dba.errors;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;


/**
 *
 **/
public class NoRowsError extends DBError
{

    public NoRowsError(@NotNull final SQLException sqlException)
    {
        super(sqlException);
    }


    public NoRowsError(@NotNull final String message, @NotNull final SQLException sqlException)
    {
        super(message, sqlException);
    }


    public NoRowsError(@NotNull final String message)
    {
        super(message);
    }
}

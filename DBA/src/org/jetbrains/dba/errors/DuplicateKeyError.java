package org.jetbrains.dba.errors;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;


/**
 *
 **/
public class DuplicateKeyError extends DBError
{

    public DuplicateKeyError(@NotNull final SQLException sqlException)
    {
        super(sqlException);
    }


    public DuplicateKeyError(@NotNull final String message, @NotNull final SQLException sqlException)
    {
        super(message, sqlException);
    }


    public DuplicateKeyError(@NotNull final String message)
    {
        super(message);
    }
}

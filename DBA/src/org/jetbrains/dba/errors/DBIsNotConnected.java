package org.jetbrains.dba.errors;

import org.jetbrains.annotations.NotNull;


/**
 *
 **/
public class DBIsNotConnected extends DBError
{
    public DBIsNotConnected(@NotNull final String message)
    {
        super(message);
    }
}

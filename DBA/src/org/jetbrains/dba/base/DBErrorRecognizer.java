package org.jetbrains.dba.base;

import org.jetbrains.dba.errors.DBError;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;


/**
 *
 **/
public interface DBErrorRecognizer
{

    @NotNull
    DBError recognizeError(@NotNull final SQLException sqlException);

}

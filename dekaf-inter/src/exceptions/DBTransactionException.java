package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



public class DBTransactionException extends DBException {

    public DBTransactionException(final @NotNull String message,
                                  final @Nullable Throwable cause) {
        super(message, cause, null);
    }
    
}

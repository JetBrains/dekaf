package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



public abstract class JdbcDriverException extends DBException {

    protected JdbcDriverException(final @NotNull String message,
                                  final @Nullable String statementText)
    {
        super(message, statementText);
    }

    protected JdbcDriverException(final @NotNull Exception exception,
                                  final @Nullable String statementText)
    {
        super(exception, statementText);
    }

    protected JdbcDriverException(final @NotNull String message,
                                  final @NotNull Exception exception,
                                  @Nullable final String statementText)
    {
        super(message, exception, statementText);
    }
}

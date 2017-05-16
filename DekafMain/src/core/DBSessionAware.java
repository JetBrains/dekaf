package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.util.Function;

import java.util.function.Consumer;



public interface DBSessionAware extends DBTransactionAware {

    /**
     * Performs the given operation in a session and returns the result.
     *
     * @param <R>       type of result.
     * @param operation operation to perform.
     * @return          the result.
     */
    <R> R inSession(@NotNull Function<@NotNull DBSession, R> operation);

    /**
     * Performs the given operation in a session.
     *
     * @param operation operation to perform.
     */
    void inSessionDo(@NotNull Consumer<@NotNull DBSession> operation);

}

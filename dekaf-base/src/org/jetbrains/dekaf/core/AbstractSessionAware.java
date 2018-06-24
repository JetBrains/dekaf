package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;



/**
 * Abstract transaction-aware service.
 *
 * @param <S> the transaction.
 */
public interface AbstractSessionAware<S> {

    /**
     * Performs the given operation in a session and returns the result.
     *
     * @param <R>       type of result.
     * @param operation operation to perform.
     * @return          the result.
     */
    <R> R inSession(@NotNull Function<@NotNull S, R> operation);

    /**
     * Performs the given operation in a session.
     *
     * @param operation operation to perform.
     */
    void inSessionDo(@NotNull Consumer<@NotNull S> operation);

}

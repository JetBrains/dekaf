package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;



/**
 * Abstract transaction-aware service.
 *
 * @param <T> the transaction.
 */
public interface AbstractTransactionAware<T> {

    /**
     * Performs the given operation in transaction and returns the result.
     *
     * @param <R>       type of result.
     * @param operation operation to perform.
     * @return          the result.
     */
    <R> R inTransaction(@NotNull Function<@NotNull T, R> operation);

    /**
     * Performs the given operation in transaction and returns the result.
     *
     * @param operation operation to perform.
     */
    void inTransactionDo(@NotNull Consumer<@NotNull T> operation);

}

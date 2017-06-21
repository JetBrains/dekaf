package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import java.util.function.Function;

import java.util.function.Consumer;



public interface DBTransactionAware {

    /**
     * Performs the given operation in transaction and returns the result.
     *
     * @param <R>       type of result.
     * @param operation operation to perform.
     * @return          the result.
     */
    <R> R inTransaction(@NotNull Function<@NotNull DBTransaction,R> operation);

    /**
     * Performs the given operation in transaction and returns the result.
     *
     * @param operation operation to perform.
     */
    void inTransactionDo(@NotNull Consumer<@NotNull DBTransaction> operation);

}

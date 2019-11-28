package org.jetbrains.dekaf.inter.intf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.inter.common.ParamDef;
import org.jetbrains.dekaf.inter.common.StatementCategory;



/**
 * Represents one query or statement execution.
 */
public interface InterSeance extends AutoCloseable {


    void prepare(@NotNull String statementText,
                 @NotNull StatementCategory category,
                 @Nullable ParamDef[] paramDefs);

    void execute(@Nullable Iterable<?> paramValues);

    /**
     * Closes the seance.
     */
    void close();

    /**
     * Check whether the seance was closed;
     * @return
     */
    boolean isClosed();
    
}

package org.jetbrains.dekaf.inter.intf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.inter.common.ParamDef;
import org.jetbrains.dekaf.inter.common.StatementCategory;



/**
 * Represents one query or statement execution.
 */
public interface InterSeance extends AutoCloseable {

    void setPortionSize(int portionSize);

    void prepare(@NotNull String statementText,
                 @NotNull StatementCategory category,
                 /*@NotNull*/ ParamDef @Nullable [] paramDefs);

    void execute(@Nullable Iterable<?> paramValues);

    boolean isPrepared();

    /**
     * Makes and returns a matrix cursor.
     * @param parameter parameter number, or 0 for the primary cursor.
     * @param baseClass
     * @return the created non-initiated cursor.
     */
    @NotNull
    <B> InterMatrixCursor<B> makeMatrixCursor(int parameter, Class<? extends B> baseClass);

    /**
     * Makes and returns a column cursor.
     * @param parameter parameter number, or 0 for the primary cursor.
     * @return the created non-initiated cursor.
     */
    @NotNull
    <C> InterColumnCursor<C> makeColumnCursor(int parameter, Class<C> cellClass);

    /**
     * Makes and returns a column cursor of primitive int.
     * @param parameter parameter number, or 0 for the primary cursor.
     * @return the created cursor.
     */
    @NotNull
    InterIntsCursor makeIntsCursor(int parameter);

    /**
     * Makes and returns a column cursor of primitive long int.
     * @param parameter parameter number, or 0 for the primary cursor.
     * @return the created cursor.
     */
    @NotNull
    InterLongsCursor makeLongsCursor(int parameter);

    /**
     * Closes the seance.
     */
    void close();

    /**
     * Check whether the seance was closed.
     * @return whether the seance was closed.
     */
    boolean isClosed();
    
}

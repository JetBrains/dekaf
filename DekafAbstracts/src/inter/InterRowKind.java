package org.jetbrains.dekaf.inter;

/**
 * Intermediate layout of a row of a query result.
 *
 * @see InterResultKind
 */
public enum InterRowKind {

    /**
     * Nothing, when the query layout is {@link InterResultKind#RES_EXISTENCE}
     */
    ROW_NONE,

    /**
     * One column in the row.
     */
    ROW_COLUMN,

    /**
     * Several columns in the row.
     */
    ROW_COLUMNS,

    /**
     * A map entry, when the query layout is {@link InterResultKind#RES_MAP}
     */
    ROW_MAP_ENTRY

}

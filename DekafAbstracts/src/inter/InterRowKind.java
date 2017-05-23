package org.jetbrains.dekaf.inter;

/**
 * Intermediate layout of a row of a query result.
 * Means how should one row of the result look.
 *
 * @see InterResultKind
 */
public enum InterRowKind {

    /**
     * Nothing, when the query layout is {@link InterResultKind#RES_EXISTENCE}
     */
    ROW_NONE,

    /**
     * A row is just a value.
     */
    ROW_ONE_VALUE,

    /**
     * A row is an array of primitives.
     */
    ROW_PRIMITIVES,

    /**
     * A row is an array of objects of the same type.
     */
    ROW_OBJECTS,

    /**
     * A row is an array when elements are objects of different type.
     */
    ROW_CORTEGE,

    /**
     * A map entry, when the query layout is {@link InterResultKind#RES_MAP}.
     */
    ROW_MAP_ENTRY

}

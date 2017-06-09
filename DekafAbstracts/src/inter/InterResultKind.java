package org.jetbrains.dekaf.inter;

/**
 * Intermediate layout of whole query result.
 *
 * @see InterRowKind
 */
public enum InterResultKind {

    /**
     * Just a flag whether the result exists.
     */
    RES_EXISTENCE,

    /**
     * Just one row.
     */
    RES_ONE_ROW,

    /**
     * Array of rows.
     */
    RES_TABLE,

    /**
     * Whole result is the array of primitives.
     */
    RES_PRIMITIVE_ARRAY,

    /**
     * Text, i.e. CSV format.
     */
    RES_TEXT

}

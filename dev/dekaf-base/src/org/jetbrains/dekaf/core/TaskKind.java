package org.jetbrains.dekaf.core;

/**
 * Database task kind.
 */
public enum TaskKind {

    /**
     * A simple command (or a call or an anonymous code block) without parameters.
     */
    TASK_COMMAND,

    /**
     * A query that returns a table-like data.
     */
    TASK_QUERY,

    /**
     * A call of a routine or an anonymous code block) with parameters.
     */
    TASK_ROUTINE,

    /**
     * A special JDBC query that provides parameters in JDBC format.
     */
    TASK_JDBC_METADATA
    
}

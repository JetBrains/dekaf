package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.sql.SqlCommand
import org.jetbrains.dekaf.sql.SqlScript
import org.jetbrains.dekaf.text.Scriptum


/**
 * Encapsulates useful method for manipulating with testing database.
 * 
 * @author Leonid Bushuev
 */
interface DBTestHelper {

    /**
     * Returns a string like " from dual" if the database requires such syntax.
     *
     * @return a string like " from dual", started with a space,
     *         or an empty string when the RDBMS allows queries like "select 1" without specified table.
     */
    fun fromSingleRowTable(): String

    /**
     * Performs a command from the scriptum.
     *
     * @param scriptum        scriptum with the command to perform.
     * @param commandName     name of the command to perform.
     */
    fun performCommand(scriptum: Scriptum, commandName: String)

    /**
     * Performs the specified command.
     *
     * @param commandText     command text.
     */
    fun performCommand(commandText: String)

    /**
     * Performs the specified command.
     *
     * @param command     command to perform.
     */
    fun performCommand(command: SqlCommand)

    /**
     * Performs a command from the scriptum.
     *
     * @param scriptum        scriptum with the command to perform.
     * @param commandName     name of the command to perform.
     */
    fun performCommand(transaction: DBTransaction,
                       scriptum: Scriptum, commandName: String)

    /**
     * Performs the specified command.
     *
     * @param commandText     command text.
     */
    fun performCommand(transaction: DBTransaction,
                       commandText: String)

    /**
     * Performs the specified command.
     *
     * @param command     command to perform.
     */
    fun performCommand(transaction: DBTransaction,
                       command: SqlCommand)

    /**
     * Performs a script from the scriptum.
     *
     * @param scriptum       scriptum with the script to perform.
     * @param scriptName     name of the script to perform.
     */
    fun performScript(scriptum: Scriptum, scriptName: String)

    /**
     * Performs the given script.
     *
     * @param script  the script to perform.
     */
    fun performScript(script: SqlScript)

    /**
     * Performs the given commands.
     * 
     * @param commands  the script to perform.
     */
    fun performScript(vararg commands: String)


    /**
     * Performs a command or a meta query (and the produced commands).
     *
     * Looks for the command with name <tt>operationName</tt> + "Command".
     * If such command exists, performs it.
     * If no such command, looks for the query with name <tt>operationName</tt> + "MetaQuery".
     * If such query exists, performs it using [.performMetaQueryCommands].
     * If neither command nor query found, raises exception.
     *
     * @param scriptum       scriptum with a command or a meta query.
     * @param operationName  root part of the command or meta query.
     */
    fun performCommandOrMetaQueryCommands(scriptum: Scriptum,
                                          operationName: String)

    /**
     * Performs a specified query from scriptum. The query should
     * produce a list of commands. These commands are to perform.
     *
     * @param scriptum           scriptum with meta query.
     * @param metaQueryName      name of the meta query.
     * @param params             parameters for meta query (not for commands).
     */
    fun performMetaQueryCommands(scriptum: Scriptum,
                                 metaQueryName: String,
                                 vararg params: Any)

    /**
     * Counts rows in a table or view.
     * If no such table or view, returns [Integer.MIN_VALUE].
     *
     * @param tableName  table name (name case/quoting like in a script).
     * @return           count of rows.
     */
    fun countTableRows(tableName: String): Int

    /**
     * Counts rows in a table or view in the given transaction.
     * If no such table or view, returns [Integer.MIN_VALUE].
     *
     * @param transaction the transaction that to use for counting.
     * @param tableName   table name (name case/quoting like in a script).
     * @return            count of rows.
     */
    fun countTableRows(transaction: DBTransaction, tableName: String): Int

    /**
     * Prepares a table or view named **<tt>X1</tt>** that contains
     * one row. It has one column named **<tt>X</tt>** with value 1.
     */
    fun prepareX1()


    /**
     * Prepares a table or view named **<tt>X1000</tt>** that contains
     * 1000 rows. It has one column named **<tt>X</tt>** with numbers
     * from 1 to 1000.
     */
    fun prepareX1000()


    /**
     * Prepares a table or view named **<tt>X1000000</tt>** that contains
     * 1000000 rows. It has one column named **<tt>X</tt>** with numbers
     * from 1 to 1000000.
     */
    fun prepareX1000000()


    /**
     * Ensures that there are no tables or view that can conflict
     * with the given names.
     * If they exist, drops them.
     *
     * @param names script names of tables or views to drop (if they exist).
     */
    fun ensureNoTableOrView(vararg names: String)


    /**
     * Drops all objects in the current schema.
     */
    fun zapSchema()

}

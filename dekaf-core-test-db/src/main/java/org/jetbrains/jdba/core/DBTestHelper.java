package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.Scriptum;
import org.jetbrains.jdba.sql.SqlCommand;
import org.jetbrains.jdba.sql.SqlScript;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public interface DBTestHelper {

  /**
   * Performs a command from the scriptum.
   * @param scriptum        scriptum with the command to perform.
   * @param commandName     name of the command to perform.
   */
  void performCommand(@NotNull Scriptum scriptum, @NotNull String commandName);

  /**
   * Performs the specified command.
   * @param commandText     command text.
   */
  void performCommand(@NotNull String commandText);

  /**
   * Performs the specified command.
   * @param command     command to perform.
   */
  void performCommand(@NotNull SqlCommand command);

  /**
   * Performs a command from the scriptum.
   * @param scriptum        scriptum with the command to perform.
   * @param commandName     name of the command to perform.
   */
  void performCommand(@NotNull DBTransaction transaction,
                      @NotNull Scriptum scriptum, @NotNull String commandName);

  /**
   * Performs the specified command.
   * @param commandText     command text.
   */
  void performCommand(@NotNull DBTransaction transaction,
                      @NotNull String commandText);

  /**
   * Performs the specified command.
   * @param command     command to perform.
   */
  void performCommand(@NotNull DBTransaction transaction,
                      @NotNull SqlCommand command);

  /**
   * Performs a script from the scriptum.
   * @param scriptum        scriptum with the script to perform.
   * @param scriptName     name of the script to perform.
   */
  void performScript(@NotNull Scriptum scriptum, @NotNull String scriptName);

  /**
   * Performs the given script.
   * @param script  the script to perform.
   */
  void performScript(@NotNull SqlScript script);

  /**
   * Performs the given commands.
   * @param commands  the script to perform.
   */
  void performScript(String... commands);


  /**
   * Performs a command or a meta query (and the produced commands).
   *
   * <p>
   * Looks for the command with name <tt>operationName</tt> + "Command".
   * If such command exists, performs it.
   * If no such command, looks for the query with name <tt>operationName</tt> + "MetaQuery".
   * If such query exists, performs it using {@link #performMetaQueryCommands}.
   * If neither command nor query found, raises exception.
   * </p>
   *
   * @param scriptum       scriptum with a command or a meta query.
   * @param operationName  root part of the command or meta query.
   */
  void performCommandOrMetaQueryCommands(@NotNull Scriptum scriptum,
                                         @NotNull String operationName);

  /**
   * Performs a specified query from scriptum. The query should
   * produce a list of commands. These commands are to perform.
   * @param scriptum           scriptum with meta query.
   * @param metaQueryName      name of the meta query.
   * @param params             parameters for meta query (not for commands).
   */
  void performMetaQueryCommands(@NotNull Scriptum scriptum,
                                @NotNull String metaQueryName,
                                Object... params);

  /**
   * Counts rows in a table or view.
   * If no such table or view, returns {@link Integer#MIN_VALUE}.
   *
   * @param tableName  table name (name case/quoting like in a script).
   * @return           count of rows.
   */
  int countTableRows(@NotNull final String tableName);

  /**
   * Counts rows in a table or view in the given transaction.
   * If no such table or view, returns {@link Integer#MIN_VALUE}.
   *
   * @param transaction the transaction that to use for counting.
   * @param tableName   table name (name case/quoting like in a script).
   * @return            count of rows.
   */
  int countTableRows(@NotNull final DBTransaction transaction, @NotNull final String tableName);

  /**
   * Prepares a table or view named <b><tt>X1</tt></b> that contains
   * one row. It has one column named <b><tt>X</tt></b> with value 1.
   */
  void prepareX1();


  /**
   * Prepares a table or view named <b><tt>X1000</tt></b> that contains
   * 1000 rows. It has one column named <b><tt>X</tt></b> with numbers
   * from 1 to 1000.
   */
  void prepareX1000();


  /**
   * Prepares a table or view named <b><tt>X1000000</tt></b> that contains
   * 1000000 rows. It has one column named <b><tt>X</tt></b> with numbers
   * from 1 to 1000000.
   */
  void prepareX1000000();


  /**
   * Ensures that there are no tables or view that can conflict
   * with the given names.
   * If they exist, drops them.
   *
   * @param names script names of tables or views to drop (if they exist).
   */
  void ensureNoTableOrView(String... names);


  /**
   * Drops all objects in the current schema.
   */
  void zapSchema();

}

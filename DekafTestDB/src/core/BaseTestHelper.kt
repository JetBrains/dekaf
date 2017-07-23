package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.exceptions.DBProtectionException
import org.jetbrains.dekaf.exceptions.NoTableOrViewException
import org.jetbrains.dekaf.sql.SqlCommand
import org.jetbrains.dekaf.sql.SqlScript
import org.jetbrains.dekaf.sql.SqlScriptBuilder
import org.jetbrains.dekaf.text.Scriptum
import org.jetbrains.dekaf.util.chopBy
import java.lang.String.format
import java.util.*
import kotlin.reflect.KClass


/**
 * @author Leonid Bushuev from JetBrains
 */
abstract class BaseTestHelper : DBTestHelper {

    protected val db: DBFacade

    protected val scriptum: Scriptum

    protected val schemasNotToZap: MutableSet<String> = TreeSet(String.CASE_INSENSITIVE_ORDER)


    protected constructor(db: DBFacade, helperClass: KClass<out BaseTestHelper>)
        : this(db, Scriptum.of(helperClass))

    protected constructor(db: DBFacade, scriptum: Scriptum) {
        this.db = db
        this.scriptum = scriptum

        schemasNotToZap.add("information_schema")
    }


    override fun fromSingleRowTable(): String {
        return ""
    }

    override fun performCommand(scriptum: Scriptum, commandName: String) {
        val command = scriptum.command(commandName)
        performCommand(command)
    }

    override fun performCommand(commandText: String) {
        val command = SqlCommand(commandText)
        performCommand(command)
    }

    override fun performCommand(command: SqlCommand) {
        db.inSessionDo { session -> session.command(command).run() }
    }

    override fun performCommand(transaction: DBTransaction,
                                scriptum: Scriptum, commandName: String) {
        val command = scriptum.command(commandName)
        performCommand(transaction, command)
    }

    override fun performCommand(transaction: DBTransaction,
                                commandText: String) {
        val command = SqlCommand(commandText)
        performCommand(transaction, command)
    }

    override fun performCommand(transaction: DBTransaction,
                                command: SqlCommand) {
        transaction.command(command).run()
    }

    override fun performScript(scriptum: Scriptum, scriptName: String) {
        val script = scriptum.script(scriptName)
        performScript(script)
    }

    override fun performScript(script: SqlScript) {
        db.inSessionDo { session -> session.script(script).run() }
    }

    override fun performScript(vararg commands: String) {
        if (commands.isEmpty()) return

        val b = SqlScriptBuilder()
        for (c in commands) b.add(c)

        performScript(b.build())
    }

    override fun performCommandOrMetaQueryCommands(scriptum: Scriptum,
                                                   operationName: String) {
        if (scriptum.findText(operationName + "Command") != null) {
            performCommand(scriptum, operationName + "Command")
        }
        else if (scriptum.findText(operationName + "MetaQuery") != null) {
            performMetaQueryCommands(scriptum, operationName + "MetaQuery")
        }
        else {
            throw IllegalArgumentException(format(
                    "The scriptum has no operation %sCommand or %sMetaQuery",
                    operationName, operationName))
        }
    }


    override fun performMetaQueryCommands(scriptum: Scriptum,
                                          metaQueryName: String,
                                          vararg params: Any?) {
        val metaQuery = scriptum.query(metaQueryName, layoutListOf(rowValueOf(String::class.java)))

        db.inSessionDo { session ->
            val commands = session.query(metaQuery).withParams(*params).run()
            if (commands != null && commands.isNotEmpty()) {
                val sb = SqlScriptBuilder()
                for (command in commands) if (command.isNotEmpty()) sb.add(command)
                val script = sb.build()
                session.script(script).run()
            }
        }
    }


    override fun countTableRows(tableName: String): Int {
        return db.inSession { session -> countTableRows(session, tableName) }
    }

    override fun countTableRows(transaction: DBTransaction, tableName: String): Int {
        val queryText = "select count(*) from " + tableName

        try {
            return transaction.query(queryText, layoutSingleValueOf(Int::class.java)).run()!!
        }
        catch (ntv: NoTableOrViewException) {
            return Integer.MIN_VALUE
        }

    }


    override fun ensureNoTableOrView(vararg names: String) {
        val namePacks = names.chopBy(4)
        for (namePack in namePacks) {
            ensureNoTableOrView4(namePack)
        }
    }

    protected open fun ensureNoTableOrView4(params: Array<out String?>) {
        performMetaQueryCommands(scriptum, "EnsureNoTableOrViewMetaQuery", *params)
    }


    override fun zapSchema() {
        val connectionInfo = db.connectionInfo
        val schemaName = connectionInfo.schemaName
        if (schemaName != null && schemasNotToZap.contains(schemaName))
            throw DBProtectionException(format("The schema %s must not be zapped", schemaName), "zapSchema")

        zapSchemaInternally(connectionInfo)
    }

    protected open fun zapSchemaInternally(connectionInfo: ConnectionInfo) {
        performCommandOrMetaQueryCommands(scriptum, "ZapSchema")
    }


}

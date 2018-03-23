package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.exceptions.UnknownDBException
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.SQLException
import java.util.*


/**
 * A generic test helper for other (unsupported) RDBMS.
 *
 * @author Leonid Bushuev
 */
class UnknownDBTestHelper(db: DBFacade) : BaseTestHelper(db, UnknownDBTestHelper::class) {

    private var initialized: Boolean = false
    private var isOracle: Boolean = false
    private var isDB2: Boolean = false
    private var isHsql: Boolean = false
    private var isDerby: Boolean = false

    private var fromSingleRowTable = ""

    init {
        if (db.isConnected) initVariables()
    }

    private fun initVariablesIfNeeded() {
        if (!initialized) {
            assert(db.isConnected) { "Expected that is connected to DB" }
            initVariables()
        }
    }

    private fun initVariables() {
        val info = db.connectionInfo
        isOracle = info.rdbmsName.startsWith("Oracle")
        isDB2 = info.rdbmsName.startsWith("DB2")
        isHsql = info.rdbmsName.startsWith("HSQL")
        isDerby = info.rdbmsName.contains("Derby")

        fromSingleRowTable = when {
            isOracle         -> " from dual"
            isDB2 || isDerby -> " from sysibm.sysdummy1"
            isHsql           -> " from information_schema.schemata limit 1"
            else             -> ""
        }
        
        initialized = true
    }

    override fun prepareX1() {
        performCommand("create or replace view X1 as select 1 as X" + fromSingleRowTable)
    }

    override fun prepareX1000() {
        performCommand(scriptum, "X10")
        performCommand(scriptum, "X1000")
    }

    override fun prepareX1000000() {
        prepareX1000()
        performCommand(scriptum, "X1000000")
    }

    override fun fromSingleRowTable(): String {
        initVariablesIfNeeded()
        return fromSingleRowTable
    }


    override fun ensureNoTableOrView(vararg names: String) {
        val tableTypes = arrayOf("TABLE", "VIEW")

        val connectionInfo = db.connectionInfo
        if (connectionInfo.databaseName == null)
            throw IllegalStateException("Cannot clean schema when the database name is unknown")
        if (connectionInfo.schemaName == null)
            throw IllegalStateException("Cannot clean schema when the schema name is unknown")

        db.inSessionDo { session ->

            for (name in names) {
                zapTables(session, connectionInfo, name, tableTypes)
            }

        }
    }

    override fun zapSchema() {
        val connectionInfo = db.connectionInfo
        if (connectionInfo.databaseName == null)
            throw IllegalStateException("Cannot clean schema when the database name is unknown")
        if (connectionInfo.schemaName == null)
            throw IllegalStateException("Cannot clean schema when the schema name is unknown")

        db.inSessionDo { session ->

            zapTables(session, connectionInfo, "%", null)

        }
    }


    private fun zapTables(session: DBSession,
                          connectionInfo: ConnectionInfo,
                          tableNameMask: String, tableTypes: Array<String>?) {
        val md = session.getSpecificService(DatabaseMetaData::class.java,
                                            ImplementationAccessibleService.Names.JDBC_METADATA) ?: throw RuntimeException("Unable to obtain metadata from unknown database")

        // obtaining tables and list of commands to execute
        val commands = ArrayList<String>()
        try {
            val tables = md.getTables(connectionInfo.databaseName,
                                      connectionInfo.schemaName,
                                      tableNameMask,
                                      tableTypes)
            try {
                while (tables.next()) {
                    val tableName = tables.getString(3)
                    var tableType: String? = tables.getString(4)
                    if (tableName == null || tableName.isEmpty() || tableType == null || tableType.isEmpty()) continue
                    tableType = tableType.toLowerCase()
                    if (tableType.contains("system")) continue
                    val command = "drop $tableType \"$tableName\""
                    commands.add(command)
                }
            }
            finally {
                tables.close()
            }
        }
        catch (e: SQLException) {
            throw UnknownDBException("Unable to list tables of unknown schema: " + e.message, e, null)
            // TODO try to recognize the error
        }

        // execute commands
        val errors = ArrayList<String>(0)
        val connection = session.getSpecificService(Connection::class.java,
                                                    ImplementationAccessibleService.Names.JDBC_CONNECTION)!!
        try {
            val stmt = connection.createStatement()
            try {
                for (command in commands) {
                    try {
                        stmt.execute(command)
                    }
                    catch (e: SQLException) {
                        val errMessage = "Failed to " + command + ": " + e.message
                        errors.add(errMessage)
                    }

                }
            }
            finally {
                stmt.close()
            }
        }
        catch (e: SQLException) {
            throw UnknownDBException("Unable to list tables of unknown schema: " + e.message, e, null)
            // TODO try to recognize the error
        }

        if (!errors.isEmpty()) {
            val b = StringBuffer(errors.size * 100)
            b.append(errors.size).append(" errors occurred when attempted to clean the schema:\n")
            for (error in errors) b.append('\t').append(error).append('\n')
            throw RuntimeException(b.toString())
        }
    }
}

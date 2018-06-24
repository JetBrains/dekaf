package org.jetbrains.dekaf

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*


/**
 * @author Leonid Bushuev
 */
object TestEnvironment {

    private val CONNECTION_STRING_VAR = "test-db"
    private val CONNECTION_STRINGS_VAR_PREFIX = "test-db-"
    private val CONNECTION_STRINGS_FILE = "test-db.properties"

    private val mySystemProperties = System.getProperties()
    private val myLocalProperties = readLocalProperties()
    private val myEnv = System.getenv()

    @JvmStatic
    fun obtainConnectionString(): String {
        val connectionString = getPossibleConnectionString(getVar(CONNECTION_STRING_VAR))
        if (connectionString == null) {
            System.err.println(
                    """|Failed to obtain Test DB connection string.
                       |The connection string is searched in the following locations:
                       |	1) '$CONNECTION_STRING_VAR' property in ./$CONNECTION_STRINGS_FILE;
                       |	2) '$CONNECTION_STRING_VAR' environment variable;
                       |	3) '$CONNECTION_STRING_VAR' system property.
                    """.trimMargin())
            System.exit(-128)
            throw RuntimeException() // just for compiler
        }
        return connectionString
    }


    @JvmStatic
    private fun getPossibleConnectionString(cs: String?): String? {
        if (cs != null) {
            val f = cs.split(":".toRegex(), 2).toTypedArray()
            if (f.size == 1) {
                val anotherPropertyName = CONNECTION_STRINGS_VAR_PREFIX + f[0]
                val anotherChance = getVar(anotherPropertyName)
                return getPossibleConnectionString(anotherChance)
            }
            else {
                return cs
            }
        }
        else {
            // TODO try to scan possible strings
            return null
        }
    }


    @JvmStatic
    fun getVar(name: String): String? {
        var value: String? = mySystemProperties.getProperty(name)
        if (value == null) value = myLocalProperties.getProperty(name)
        if (value == null) value = myEnv[name]
        return value
    }

    @Deprecated("Use the '?:' operator instead", ReplaceWith("getVar(name) ?: defaultValue", "org.jetbrains.dekaf.TestEnvironment.getVar"))
    @JvmStatic
    fun getVar(name: String, defaultValue: String): String = getVar(name) ?: defaultValue


    @JvmStatic
    private fun readLocalProperties(): Properties {
        val p = Properties()
        var dir: File? = currentDirectory
        while (dir != null && dir.isDirectory) {
            val file = File(dir, CONNECTION_STRINGS_FILE)
            if (file.exists() && !file.isDirectory) {
                readLocalPropertiesFromFile(p, file)
                break
            }
            else {
                dir = dir.parentFile
            }
        }
        return p
    }

    @JvmStatic
    private val currentDirectory: File
        get() {
            var dir = File(".")
            try {
                dir = dir.canonicalFile
            }
            catch (ioe: IOException) {
                ioe.printStackTrace()
            }

            return dir
        }

    @JvmStatic
    private fun readLocalPropertiesFromFile(p: Properties, localPropertiesFile: File) {
        try {
            val `is` = FileInputStream(localPropertiesFile)
            try {
                p.load(`is`)
            }
            finally {
                `is`.close()
            }
        }
        catch (io: IOException) {
            System.err.println("Failed to read file " + localPropertiesFile.path + ": " + io.message)
        }

    }


}

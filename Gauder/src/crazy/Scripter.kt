package org.jetbrains.dekaf.crazy

import org.jetbrains.dekaf.util.nameStr
import java.nio.file.Files
import java.nio.file.Path


class Scripter (

        val model: Model

) {

    val eos = "/\n\n"

    fun generate(): List<String> {
        val script = ArrayList<String>()
        for (major in model.majors) {
            val stmt = when (major) {
                is Model.Table -> generateTable(major)
                else           -> null
            }
            if (stmt != null) script.add(stmt.toString())
        }
        return script
    }


    private fun generateTable(table: Model.Table): CharSequence? {
        val b = StringBuilder()
        b.append("create table ${table.name} (\n")
        val elements = generateTableInnerElements(table)
        val n = elements.size
        for (i in 0..n-1) {
            val e = elements[i]
            b.append('\t').append(e)
            if (i < n-1) b.append(',')
            b.append('\n')
        }
        b.append(")\n")
        return b
    }

    private fun generateTableInnerElements(table: Model.Table): List<String> {
        val elements = ArrayList<String>(table.columns.size + 1)
        for (column in table.columns) {
            val nullability = if (column.isMandatory) " not null" else ""
            elements.add("${column.name} ${column.dataType}$nullability")
        }
        if (table.primaryKey != null) {
            elements.add("primary key (${table.primaryKey!!.columnNamesStr})")
        }
        for (fk in table.foreignKeys) {
            var phrase = "foreign key (${fk.columns.nameStr}) references ${fk.masterKey.table.name}"
            if (fk.cascade) phrase = "$phrase on delete cascade"
            if (fk.name != "__no_name__") phrase = "constraint ${fk.name} $phrase"
            elements.add(phrase)
        }
        return elements
    }


    fun writeToFile(file: Path, script: Iterable<String>) {
        Files.newBufferedWriter(file, Charsets.UTF_8)
                .use { writer ->
                    for (s in script) {
                        writer.write(s)
                        writer.write(eos)
                    }
                    writer.flush()
                }
    }

}
package org.jetbrains.dekaf.crazy

import org.jetbrains.dekaf.util.abb
import org.jetbrains.dekaf.util.say
import org.jetbrains.dekaf.util.selectOne
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class Contriver (

        val model: Model,
        val dictionary: Dictionary

) {

    val topTableNumber = 10
    val inheritedTableNumber = 25
    val detailTableNumber = 25
    val auxiliaryColumnsMinNumber = 1
    val auxiliaryColumnsMaxNumber = 9

    private val random = Random(System.nanoTime() * System.currentTimeMillis())

    val usedNouns    = HashSet<String>()
    val primaryNouns = HashSet<String>()

    val errors = ArrayList<String>()


    fun invent() {

        inventTopTables()
        inventInheritedTables()
        inventDetailTables()
        report()

    }

    private fun inventTopTables() {
        val tables = ArrayList<Model.Table>()
        for (k in 1..topTableNumber) {
            try {
                val f = random.nextDouble()
                val noun = dictionary.nouns.filter { it !in usedNouns && it.length >= 6 }.selectOne(f)
                val abb = noun.abb()
                val table = model.Table()
                usedNouns.add(noun)
                usedNouns.add(abb)
                table.primaryWord = noun
                table.abb = noun.abb()
                table.name = noun
                tables.add(table)
            }
            catch (e: Exception) {
                handleError(e)
            }
        }

        primaryNouns.addAll(usedNouns)

        for (table in tables) {
            inventPrimaryKey(table)
            inventTableAuxiliaryColumns(table)
        }
    }

    private fun inventPrimaryKey(table: Model.Table) {
        val x = inventColumn(Inspiration.primaryColumns)
        val column = model.Column(table, table.abb + '_' + x.first, x.second, true, true)
        val key = model.Key(table, true)
        key.columns.add(column)
    }

    private fun inventInheritedTables() {
        val tables = ArrayList<Model.Table>(topTableNumber + inheritedTableNumber)
        tables.addAll(model.majors.filter{it is Model.Table}.map{it as Model.Table})

        for (k in 1..inheritedTableNumber) {
            // make a table
            val f1 = random.nextDouble()
            val adjective = dictionary.adjectives.filter{it.length <= 15}.selectOne(f1)
            val f2 = random.nextDouble()
            val baseTable = tables.filter{it.primaryKey != null}.selectOne(f2)
            val tableName = adjective + '_' + baseTable.name
            if (tableName.length > 30) continue
            val table = model.Table()
            table.baseTable = baseTable
            table.name = tableName
            table.abb = tableName.abb()

            val key = model.Key(table, primary = true)

            // copy primary key columns of the base table
            for (bc in baseTable.primaryKey!!.columns) {
                val column = bc.copy(table, isPrimary = true)
                key.columns.add(column)
            }

            // auxiliary columns
            inventTableAuxiliaryColumns(table)

            // ok
            tables.add(table)
        }
    }

    private fun inventDetailTables() {
        val tables = ArrayList<Model.Table>(topTableNumber + detailTableNumber)
        tables.addAll(model.majors.filter{it is Model.Table}.map{it as Model.Table})

        for (k in 1..detailTableNumber) {
            // make a table
            val f1 = random.nextDouble()
            val word = dictionary.nouns.filter{it !in primaryNouns && it.length <= 10}.selectOne(f1)
            val f2 = random.nextDouble()
            val parentTable = tables.filter{it.primaryKey != null}.selectOne(f2)
            val tableName = parentTable.name + '_' + word
            val abb = word.abb()
            if (tableName.length > 30) continue
            if (tableName in usedNouns) continue
            val table = model.Table()
            table.parentTable = parentTable
            table.name = tableName
            table.abb = abb

            val key = model.Key(table, primary = true)

            // copy primary key columns of the parent table
            for (pc in parentTable.primaryKey!!.columns) {
                val column = pc.copy(table, isPrimary = true)
                key.columns.add(column)
            }

            // add one more primary key column
            val x = inventColumn(Inspiration.secondaryColumns)
            val xColumn = model.Column(table, abb + '_' + x.first, x.second, true, true)
            key.columns.add(xColumn)

            // auxiliary columns
            inventTableAuxiliaryColumns(table)

            // ok
            tables.add(table)
        }

    }


    private fun inventTableAuxiliaryColumns(table: Model.Table) {
        val n = auxiliaryColumnsMinNumber + random.nextInt(auxiliaryColumnsMaxNumber - auxiliaryColumnsMinNumber)
        for (i in 1..n) {
            val f1 = random.nextDouble()
            val f2 = random.nextDouble()
            val f3 = random.nextDouble()
            val noun = dictionary.nouns.filter { it !in primaryNouns && it.length >= 4 }.selectOne(f1)
            val type = adjustDataType(Inspiration.auxiliaryTypes.selectOne(f2))
            val mandatory = (type.contains("number") || type.contains("char")) && f3 <= 0.4
            val columnName = table.abb + '_' + noun
            if (table.columnByName(columnName) != null) continue
            val column = model.Column(table, columnName, type, mandatory)
        }
    }

    private fun inventColumn(plenty: Map<String,String>): Pair<String,String> {
        val f = random.nextDouble()
        val name = plenty.keys.selectOne(f)
        var type = plenty[name]!!
        type = adjustDataType(type)
        return name to type
    }

    private fun adjustDataType(type: String): String {
        var t = type
        if (t.contains("(C)")) t = t.replace("C", "${random.nextInt(6) + 1}")
        if (t.contains("(N)")) t = t.replace("N", "${random.nextInt(18) + 1}")
        if (t.contains("(V)")) t = t.replace("V", "${random.nextInt(60) + 11}")
        if (t.contains("(W)")) t = t.replace("W", "${random.nextInt(160) + 21}")
        return t
    }


    private fun report() {
        val tables = model.majors.count{it is Model.Table}
        val columns = model.majors.filter{it is Model.Table}.map{it as Model.Table}.sumBy{it.columns.size}
        say("Generated $tables tables with $columns columns.")
        say("Used ${usedNouns.size} nouns and abbreviation.")

        if (errors.size == 0) {
            say("Completed successfully.")
        }
        else {
            say("Completed with ${errors.size} errors!")
        }
    }


    private fun handleError(e: Exception) {
        handleError(e.message ?: e.javaClass.simpleName)
    }

    private fun handleError(m: String) {
        errors.add(m)
    }

}
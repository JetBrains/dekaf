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

    val topTableNumber = 25
    val inheritedTableNumber = 25
    val detailTableNumber = 25
    val mmTableNumber = 20
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
        inventManyToManyTables()
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
                table.nameWords.add(noun)
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
        val column = model.Column(table, listOf(table.abb!!, x.first), x.second, true, true)
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
            val table = model.Table()
            table.baseTable = baseTable
            table.nameWords.add(adjective)
            table.nameWords.addAll(baseTable.nameWords)
            table.adjustName()
            table.abb = table.name.abb()
            if (table.name.length > 30) { table.drop(); continue }


            // copy primary key columns of the base table
            val key = model.Key(table, primary = true)
            val masterKey = baseTable.primaryKey!!
            val fk1 = model.ForeignKey(table, masterKey, true)
            for (bc in masterKey.columns) {
                val column = bc.copy(table, isPrimary = true)
                key.columns.add(column)
                fk1.columns.add(column)
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
            val table = model.Table()
            table.parentTable = parentTable
            table.nameWords.addAll(parentTable.nameWords)
            table.nameWords.add(word)
            table.adjustName()
            table.abb = word.abb()
            if (table.name.length > 30) { table.drop(); continue }

            // copy primary key columns of the parent table and make a foreign key
            val key = model.Key(table, primary = true)
            val masterKey = parentTable.primaryKey!!
            val fk1 = model.ForeignKey(table, masterKey, true)
            for (pc in masterKey.columns) {
                val column = pc.copy(table, isPrimary = true)
                key.columns.add(column)
                fk1.columns.add(column)
            }

            // add one more primary key column
            val x = inventColumn(Inspiration.secondaryColumns)
            val xColumn = model.Column(table, listOf(table.abb!!, x.first), x.second, true, true)
            key.columns.add(xColumn)

            // auxiliary columns
            inventTableAuxiliaryColumns(table)

            // ok
            tables.add(table)
        }

    }


    private fun inventManyToManyTables()  {
        val tables = model.majors.filter{it is Model.Table && it.primaryKey != null}.map{it as Model.Table}

        mm@
        for (k in 1..mmTableNumber) {
            val tab1 = tables.selectOne(random.nextDouble())
            val tab2 = tables.selectOne(random.nextDouble())
            if (tab1 == tab2) continue
            val pk1 = tab1.primaryKey!!
            val pk2 = tab2.primaryKey!!

            val nameWords = ArrayList<String>()
            nameWords.addAll(tab1.nameWords)
            for (word in tab2.nameWords) if (word !in nameWords) nameWords.add(word)

            val table = model.Table()
            table.nameWords.addAll(nameWords)
            table.adjustName()
            val fk1 = model.ForeignKey(table, pk1)
            val fk2 = model.ForeignKey(table, pk2)

            for (c in pk1.columns) {
                val column = c.copy(table)
                fk1.columns.add(column)
            }
            for (c in pk2.columns) {
                val c1 = table.columnByName(c.name)
                if (c1 != null && c1.dataType != c.dataType) { table.drop(); continue@mm }
                val column = c1 ?: c.copy(table)
                fk2.columns.add(column)
            }
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
            val column = model.Column(table, listOf(table.abb!!, noun), type, mandatory)
            if (table.columnByName(column.name) != column) { column.drop(); continue }
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
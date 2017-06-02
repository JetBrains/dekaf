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

    val topTableNumber = 500
    val inheritedTableNumber = 240
    val detailTableNumber = 250
    val mmTableNumber = 250
    val auxiliaryColumnsMinNumber = 1
    val auxiliaryColumnsMaxNumber = 39
    val viewsNumber1 = 1100
    val packsNumber1 = 1100
    val hacksNumber = 2100
    val hacksProcNumberMin = 10
    val hacksProcNumberMax = 60


    private val random = Random(System.nanoTime() * System.currentTimeMillis())

    val usedNouns    = HashSet<String>()
    val primaryNouns = HashSet<String>()

    val errors = ArrayList<String>()


    fun invent() {

        inventTopTables()
        inventInheritedTables()
        inventDetailTables()
        inventManyToManyTables()
        inventViews1()
        inventPacks1()
        inventPacks2()
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
                table.addNameWord(noun)
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
            table.addNameWord(adjective)
            table.addNameWords(baseTable.nameWords)
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
            table.setNameWords(parentTable.nameWords)
            table.addNameWord(word)
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
            table.setNameWords(nameWords)
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


    private fun inventViews1() {
        val tables = model.majors.filter{it is Model.Table && it.baseTable != null && it.nameLength <= 26}.map{it as Model.Table}

        for (k in 1..viewsNumber1) {
            val f = random.nextDouble()
            val table2 = tables.selectOne(f)
            val table1 = table2.baseTable ?: continue
            val view = model.View()
            view.setNameWords(table2.nameWords)
            view.addNameWord(k.toString())

            val text = """|create view ${view.name} as
                          |select *
                          |from ${table1.name} natural join ${table2.name}
                          |order by 1,2
                       """.trimMargin()
            view.text = text.trim() + '\n'
        }

    }


    private fun inventPacks1() {
        val tables = model.majors.filter{it is Model.Table && it.primaryKey != null && it.nameLength <= 22}.map{it as Model.Table}

        for ((index, table) in tables.withIndex()) {
            if (index >= packsNumber1) break

            val prima = table.primaryKey!!
            val primaColumns = prima.columns
            val otherColumns = table.columns.minus(prima.columns)

            val pack = model.Package(table.nameWords)
            pack.addNameWord("working")

            val keySpec = primaColumns.joinToString { it.name + ' ' + it.formalType() }
            val specCreate = "procedure createOne($keySpec);"

            val specProcedures = ArrayList<String>()
            for (column in otherColumns) {
                val spec = "procedure set_${column.name} ($keySpec, ${column.name} ${column.formalType()});"
                specProcedures.add(spec)
            }

            val text1 = """|create package ${pack.name} as
                           |
                           |$specCreate
                           |
                           |${specProcedures.joinToString("\n")}
                           |
                           |end ${pack.name};
                           |""".trimMargin()

            pack.text1 = text1
        }
    }


    private fun inventPacks2() {
        val tables = model.majors.filter{it is Model.Table && !it.isTop && it.primaryKey != null && it.nameLength <= 22}.map{it as Model.Table}

        val usedPackNames = HashSet<String>()

        for (k in 1..hacksNumber) {
            val table = tables.selectOne(random.nextDouble())

            val prima = table.primaryKey!!
            val primaColumns = prima.columns
            val otherColumns = table.columns.minus(prima.columns)

            val pack = model.Package(table.nameWords)
            pack.addNameWord("hacking")
            if (pack.name in usedPackNames) pack.addNameWord(k.toString())
            usedPackNames.add(pack.name)

            val keySpec = primaColumns.joinToString { it.name + ' ' + it.formalType() }

            val usedProcNames = HashSet<String>()

            val hackProcedures = ArrayList<String>()
            val n = hacksProcNumberMin + ((hacksProcNumberMax-hacksProcNumberMin) * random.nextFloat()).toInt()
            for (h in 1..hacksProcNumberMin) {
                val column = otherColumns.selectOne(random.nextDouble())
                val verb = dictionary.verbs.selectOne(random.nextDouble())
                val procName = "${verb}_${column.name}"
                if (procName in usedProcNames) continue
                usedProcNames.add(procName)
                val spec = "procedure $procName ($keySpec, ${column.name} ${column.formalType()});"
                hackProcedures.add(spec)
            }

            val text1 = """|create package ${pack.name} as
                           |
                           |${hackProcedures.joinToString("\n")}
                           |
                           |end ${pack.name};
                           |""".trimMargin()

            pack.text1 = text1
        }
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
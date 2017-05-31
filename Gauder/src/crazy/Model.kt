package org.jetbrains.dekaf.crazy


/**
 * Crazy Model.
 */
class Model {

    var counter: Int = 0

    val majors = ArrayList<Major>()


    /// MATTER \\\

    abstract inner class Matter {

        val priority:  Int                 = ++counter
        val nameWords: MutableList<String> = ArrayList<String>(4)

        val name:       String  get() = if (nameWords.isNotEmpty()) nameWords.joinToString("_") else "__no_name__"
        val nameLength: Int     get() = nameWords.sumBy { it.length } + (if (nameWords.size >= 2) nameWords.size-1 else 0)

        fun adjustName() {
            if (nameWords.size > 1 && nameLength > 30) {
                for (i in nameWords.size-1 downTo 1) nameWords.removeAt(i)
                nameWords.add(priority.toString())
            }
        }
    }


    abstract inner class Major: Matter() {

    }


    abstract inner class LikeTable: Major() {

        val columns = ArrayList<Column>()

        fun columnByName(name: String) = columns.firstOrNull { it.name == name }

        fun drop() {
            majors.remove(this)
        }
    }


    inner class Table: LikeTable() {

        var primaryWord:      String? = null
        var abb:              String? = null

        var baseTable:        Table? = null
        var parentTable:      Table? = null
        var primaryKey:       Key?   = null

        var alternativeKeys = ArrayList<Key>()
        var indices         = ArrayList<Key>()
        var foreignKeys     = ArrayList<ForeignKey>()

        val isTop       get() = baseTable == null && parentTable == null
        val isInherited get() = baseTable != null
        val isDetail    get() = parentTable != null

        init {
            majors.add(this)
        }
    }


    inner class Key: Matter {

        val table    : Table
        val columns  = ArrayList<Column>()
        var primary  : Boolean


        constructor(table: Table, primary: Boolean = false) : super() {
            this.table = table
            this.primary = primary

            if (primary) {
                assert(table.primaryKey == null) {"Table ${table.name} already has a primary key"}
                table.primaryKey = this
            }
            else {
                // TODO
            }
        }

        val columnNames:    List<String>  get() = columns.map { it.name }
        val columnNamesStr: String        get() = columns.joinToString(separator = ",") { it.name }
    }


    inner class ForeignKey: Matter {

        val table     : Table
        val masterKey : Key
        val columns   = ArrayList<Column>()

        var cascade   : Boolean

        constructor(table: Table, masterKey: Key, cascade: Boolean = false) : super() {
            this.table = table
            this.masterKey = masterKey
            table.foreignKeys.add(this)
            this.cascade = cascade
        }
    }


    inner class Column: Matter {

        val table       : LikeTable

        var dataType    : String
        var isMandatory : Boolean = false
        var isPrimary   : Boolean = false

        constructor(table: LikeTable, nameWords: Collection<String>, dataType: String, isMandatory: Boolean = false, isPrimary: Boolean = false) : super() {
            this.table = table
            this.nameWords.addAll(nameWords)
            this.dataType = dataType
            this.isMandatory = isMandatory || isPrimary
            this.isPrimary = isPrimary
            table.columns.add(this)
        }

        fun copy(table: LikeTable, isPrimary: Boolean = false): Column =
                Column(table, nameWords, dataType, isMandatory || isPrimary, isPrimary)

        fun drop() {
            table.columns.remove(this)
        }
    }


}
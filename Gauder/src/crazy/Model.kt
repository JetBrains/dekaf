package org.jetbrains.dekaf.crazy


/**
 * Crazy Model.
 */
class Model {

    var counter: Int = 0

    val majors = ArrayList<Major>()
    

    /// MATTER \\\

    abstract inner class Matter {

        val priority: Int = ++counter
        var name:     String = "__no_name__"

    }


    abstract inner class Major: Matter() {

    }


    abstract inner class LikeTable: Major() {

        val columns = ArrayList<Column>()

        fun columnByName(name: String) = columns.firstOrNull { it.name == name }

    }


    inner class Table: LikeTable() {

        var primaryWord: String? = null
        var abb:         String? = null

        var baseTable:   Table? = null
        var parentTable: Table? = null
        var primaryKey:  Key?   = null

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


    inner class Column: Matter {

        val table       : LikeTable

        var dataType    : String
        var isMandatory : Boolean = false
        var isPrimary   : Boolean = false

        constructor(table: LikeTable, name: String, dataType: String, isMandatory: Boolean = false, isPrimary: Boolean = false) : super() {
            this.table = table
            this.name = name
            this.dataType = dataType
            this.isMandatory = isMandatory || isPrimary
            this.isPrimary = isPrimary
            table.columns.add(this)
        }

        fun copy(table: LikeTable, isPrimary: Boolean = false): Column =
                Column(table, name, dataType, isMandatory || isPrimary, isPrimary)
    }


}
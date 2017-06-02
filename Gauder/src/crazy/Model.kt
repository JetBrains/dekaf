package org.jetbrains.dekaf.crazy


/**
 * Crazy Model.
 */
class Model {

    var counter: Int = 0

    val majors = ArrayList<Major>()



    /// MATTER \\\

    abstract inner class Matter {

        val priority:  Int = ++counter

        private val nameWords_: MutableList<String> = ArrayList<String>(4)
        private var name_     : String              = "__no_name__"

        val nameWords:  List<String>   get() = nameWords_
        val name:       String         get() = name_
        val hasName:    Boolean        get() = nameWords_.isNotEmpty()
        val nameLength: Int            get() = name_.length

        fun addNameWord(word: String) {
            nameWords_.add(word)
            adjustName()
        }

        fun addNameWords(words: Iterable<String>) {
            for (word in words) nameWords_.add(word)
            adjustName()
        }

        fun setNameWords(words: Iterable<String>) {
            nameWords_.clear()
            for (word in words) nameWords_.add(word)
            adjustName()
        }

        fun clearName() {
            nameWords_.clear()
            adjustName()
        }

        private fun adjustName() {
            if (nameWords_.isEmpty()) {
                this.name_ = "__no_name__"
                return
            }

            var name: String
            var n = nameWords.size
            do {
                name = nameWords_.joinToString(separator = "_", limit = n, truncated = "")
                if (name.endsWith('_')) name = name.substring(0, name.length-1) // workaround the bug KT-182489
                n--
            } while (n > 0 && name.length > 30)

            this.name_ = name
        }

        override fun toString() = "${this.javaClass.simpleName.toLowerCase()} $name"
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


    inner class View: LikeTable {

        var text: String

        constructor(nameWords: Iterable<String> = emptyList(), text: String = "select 1") : super() {
            this.setNameWords(nameWords)
            this.text = text
            majors.add(this)
        }
    }

    inner class Package : Major {

        var text1: String? = null
        var text2: String? = null

        constructor(nameWords: Iterable<String> = emptyList()) : super() {
            this.setNameWords(nameWords)
            majors.add(this)
        }
    }


    inner class Column: Matter {

        val table       : LikeTable

        var dataType    : String
        var isMandatory : Boolean = false
        var isPrimary   : Boolean = false

        constructor(table: LikeTable, nameWords: Iterable<String>, dataType: String, isMandatory: Boolean = false, isPrimary: Boolean = false) : super() {
            this.table = table
            this.setNameWords(nameWords)
            this.dataType = dataType
            this.isMandatory = isMandatory || isPrimary
            this.isPrimary = isPrimary
            table.columns.add(this)
        }

        fun formalType(): String {
            var s = dataType
            val p = s.indexOf('(')
            if (p > 0) s = s.substring(0,p).trim()
            return s
        }

        fun copy(table: LikeTable, isPrimary: Boolean = false): Column =
                Column(table, nameWords, dataType, isMandatory || isPrimary, isPrimary)

        fun drop() {
            table.columns.remove(this)
        }
    }


}
package org.jetbrains.dekaf.sql

import org.jetbrains.dekaf.text.Scriptum
import org.jetbrains.dekaf.text.TextFileFragment
import org.jetbrains.dekaf.text.TextFragment

import java.util.function.Function


/**
 * SQL statement.
 * 
 * @author Leonid Bushuev 
 */
abstract class SqlStatement {

    /// STATE \\\

    /**
     * Number of lines skipped from source text.
     * Zero if nothing skipped.
     */
    val row: Int

    /**
     * Query source text.
     */
    val sourceText: String

    /**
     * The natural name of the statement.
     * Used, for example, when the statement was got from the [Scriptum].
     */
    val name: String?

    /**
     * A short (one line) description of this SQL statement.
     * It may contain a file, position and optional name of this SQL statement.
     */
    val description: String


    /// CONSTRUCTORS \\\

    protected constructor(sourceFragment: TextFragment) {
        sourceText = sourceFragment.text
        row = sourceFragment.row

        val mainDescriptionPart: String
        if (sourceFragment is TextFileFragment) {
            val f = sourceFragment
            var name = f.fragmentName
            this.name = name
            if (name == null) name = "SQL fragment"
            mainDescriptionPart = name + " from " + f.textName + ':' + f.row + ':' + f.pos
        }
        else {

            val f = sourceFragment
            mainDescriptionPart = "SQL statement from " + f.textName + ':' + f.row + ':' + f.pos
            name = null
        }

        // TODO add some additional info to the mainDescriptionPart
        description = mainDescriptionPart
    }


    @JvmOverloads
    protected constructor(sourceText: String,
                          row: Int = 1,
                          statementName: String? = null)
            : this(
                    row,
                    sourceText,
                    statementName ?: '@' + Integer.toString(row),
                    statementName ?: "SQL statement at row " + row
            )

    /**
     * Trivial constructor.
     * @param row           row number (starting from 1)
     * @param sourceText
     * @param name
     * @param description
     */
    protected constructor(row: Int,
                          sourceText: String,
                          name: String?,
                          description: String) {
        this.row = row
        this.sourceText = sourceText
        this.name = name
        this.description = description
    }


    /// MANIPULATION AND MUTATION \\\

    abstract fun rewrite(operator: Function<String, String>): SqlStatement


    /// LEGACY FUNCTIONS \\\

    override fun toString(): String {
        val b = StringBuilder()
        if (name != null) b.append(name).append(": ")
        b.append(description).append(":\n")
        b.append(sourceText)
        return b.toString()
    }
}

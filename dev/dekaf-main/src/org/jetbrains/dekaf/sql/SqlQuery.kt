package org.jetbrains.dekaf.sql

import org.jetbrains.dekaf.core.QueryResultLayout
import org.jetbrains.dekaf.text.TextFragment

import java.util.function.Function


/**
 * A SQL query that returns a list of rows.
 *
 * @param <S> type of the query result.
 *
 * @author Leonid Bushuev
 */
class SqlQuery<S> : SqlStatement {


    /// STATE \\\

    val layout: QueryResultLayout<S>

    @Transient private var myDisplayName: String? = null


    /// CONSTRUCTORS \\\

    constructor(sourceFragment: TextFragment,
                layout: QueryResultLayout<S>) : super(sourceFragment) {
        this.layout = layout
    }

    constructor(sourceText: String,
                layout: QueryResultLayout<S>) : super(sourceText) {
        this.layout = layout
    }

    constructor(row: Int,
                sourceText: String,
                name: String?,
                description: String,
                layout: QueryResultLayout<S>,
                displayName: String) : super(row, sourceText, name, description) {
        this.layout = layout
        this.myDisplayName = displayName
    }

    var displayName: String
        get() {
            if (myDisplayName == null) {
                prepareDisplayName()
            }
            return myDisplayName!!
        }
        set(displayName) {
            this.myDisplayName = displayName
        }

    @Synchronized
    private fun prepareDisplayName() {
        if (myDisplayName != null) return

        val nl = sourceText.indexOf('\n')
        var str1 = if (nl > 0) sourceText.substring(0, nl) else sourceText
        str1 = str1.trim { it <= ' ' }

        myDisplayName = str1
    }


    /// MANIPULATING FUNCTIONS \\\

    override fun rewrite(operator: Function<String, String>): SqlQuery<S> {
        val transformedSourceText = operator.apply(sourceText)
        return SqlQuery(
                row,
                transformedSourceText,
                name,
                description,
                layout,
                description
        )
    }


    //// LEGACY FUNCTIONS \\\\

    override fun toString(): String {
        return displayName
    }
}

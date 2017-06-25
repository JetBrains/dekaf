package org.jetbrains.dekaf.sql

import org.jetbrains.dekaf.text.TextFragment

import java.util.function.Function


/**
 * A SQL command.
 *
 * @author Leonid Bushuev
 */
class SqlCommand : SqlStatement {


    constructor(sourceFragment: TextFragment) : super(sourceFragment)


    constructor(sourceText: String, row: Int, commandName: String?) : super(sourceText, row, commandName)


    constructor(sourceText: String) : super(sourceText)


    private constructor(row: Int,
                        sourceText: String,
                        name: String?,
                        description: String)
            : super(row, sourceText, name, description)


    override fun rewrite(operator: Function<String, String>): SqlCommand {
        val transformedSourceTex = operator.apply(sourceText)
        return SqlCommand(row, transformedSourceTex, name, description)
    }

}

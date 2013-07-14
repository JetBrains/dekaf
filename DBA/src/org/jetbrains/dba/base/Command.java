package org.jetbrains.dba.base;

import org.jetbrains.annotations.NotNull;


/**
 * A SQL command.
 * @author Leonid Bushuev from JetBrains
 */
public class Command
{

    final int lineOffset;

    @NotNull
    final String sourceText;



    public Command(final int lineOffset, @NotNull final String sourceText)
    {
        this.lineOffset = lineOffset;
        this.sourceText = sourceText;
    }


    public Command(@NotNull final String sourceText)
    {
        this(0, sourceText);
    }

}

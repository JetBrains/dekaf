package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.TaskKind;

import java.io.Serializable;



public final class InterTask implements Serializable {

    @NotNull
    public final TaskKind kind;

    @NotNull
    public final String text;


    public InterTask(final @NotNull TaskKind kind, final @NotNull String text) {
        this.kind = kind;
        this.text = text;
    }
    
}

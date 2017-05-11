package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;



public final class InterCursorLayout implements Serializable {

    @NotNull
    public final InterResultKind queryLayout;

    @NotNull
    public final InterRowKind rowLayout;

    @Nullable
    public final Class[] columnClasses;


    public InterCursorLayout(final @NotNull InterResultKind queryLayout,
                             final @NotNull InterRowKind rowLayout,
                             final @Nullable Class[] columnClasses) {
        this.queryLayout = queryLayout;
        this.rowLayout = rowLayout;
        this.columnClasses = columnClasses;
    }
}

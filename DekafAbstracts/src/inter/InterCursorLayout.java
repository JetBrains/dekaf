package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.util.NameAndClass;

import java.io.Serializable;



public final class InterCursorLayout implements Serializable {

    @NotNull
    public final InterResultKind resultLayout;

    @NotNull
    public final InterRowKind rowLayout;

    @Nullable
    public final NameAndClass[] columns;


    public InterCursorLayout(final @NotNull InterResultKind resultLayout,
                             final @NotNull InterRowKind rowLayout,
                             final @Nullable NameAndClass[] columns) {
        this.resultLayout = resultLayout;
        this.rowLayout = rowLayout;
        this.columns = columns;
    }

}

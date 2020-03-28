package org.jetbrains.dekaf.inter.intf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



public interface InterMatrixCursor<B> extends InterCursor {

    void prepare(final @NotNull Class<? extends B> @NotNull [] cellClasses);

    @Nullable
    B[][] fetchPortion();

    @Nullable
    B[] fetchRow();

}

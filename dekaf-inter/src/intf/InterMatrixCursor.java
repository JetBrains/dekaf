package org.jetbrains.dekaf.inter.intf;

import org.jetbrains.annotations.NotNull;



public interface InterMatrixCursor<B> extends InterCursor {

    void prepare();

    void prepare(final @NotNull Class<? extends B> @NotNull [] cellClasses);

    void setDefaultValue(B value);

    B[][] fetchPortion();

    B[] fetchRow();

}

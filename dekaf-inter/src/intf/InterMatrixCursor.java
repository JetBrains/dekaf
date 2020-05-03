package org.jetbrains.dekaf.inter.intf;

import org.jetbrains.annotations.NotNull;



public interface InterMatrixCursor<B> extends InterCursor {

    void prepare();

    void prepare(final @NotNull Class<?> @NotNull [] cellClasses);

    void setDefaultValue(B value);

    B[][] fetchPortion();

    B[] fetchRow();

}

package org.jetbrains.dekaf.inter.intf;

import org.jetbrains.annotations.Nullable;



public interface InterColumnCursor<C> extends InterCursor {

    void prepare();

    @Nullable
    C[] fetchPortion();

    @Nullable
    C[] fetchRow();

}

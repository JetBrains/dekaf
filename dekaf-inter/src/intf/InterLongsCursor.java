package org.jetbrains.dekaf.inter.intf;

import org.jetbrains.annotations.Nullable;



public interface InterLongsCursor extends InterCursor {

    void setDefaultValue(long defaultValue);

    @Nullable
    long[] fetchPortion();

    @Nullable
    long[] fetchRow();

}

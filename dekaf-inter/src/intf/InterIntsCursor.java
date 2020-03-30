package org.jetbrains.dekaf.inter.intf;

import org.jetbrains.annotations.Nullable;



public interface InterIntsCursor extends InterCursor {

    void setDefaultValue(int defaultValue);

    @Nullable
    int[] fetchPortion();

    @Nullable
    int[] fetchRow();

}

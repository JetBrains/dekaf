package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;



public interface InterCursor {

    /// SETTING UP \\\

    public void setPortionSize(final int portionSize);


    /// RETRIEVING DATA \\\

    @Nullable
    Serializable retrievePortion();



    /// CLOSING \\\

    void close();


}

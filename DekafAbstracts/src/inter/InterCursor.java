package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;



public interface InterCursor {

    ////// RETRIEVING DATA \\\\\\

    @Nullable
    Serializable retrievePortion();



    ////// CLOSING \\\\\\

    void close();


}

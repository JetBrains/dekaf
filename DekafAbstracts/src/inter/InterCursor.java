package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.ImplementationAccessibleService;

import java.io.Serializable;



public interface InterCursor extends ImplementationAccessibleService {

    /// SETTING UP \\\

    void setPortionSize(final int portionSize);


    /// RETRIEVING DATA \\\

    @Nullable
    Serializable retrievePortion();



    /// CLOSING \\\

    void close();

}

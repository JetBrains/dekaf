package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.ImplementationAccessibleService;



public interface InterSeance extends ImplementationAccessibleService {

    ////// EXECUTION \\\\\\

    void prepare(InterTask task);

    void assignParameters(Object[] parameters);

    void execute();

    int getAffectedRowsCount();


    ////// CURSORS \\\\\\

    @Nullable
    InterCursor openCursor(byte parameter, @NotNull InterLayout layout);


    ////// CLOSING \\\\\\

    void close();

}

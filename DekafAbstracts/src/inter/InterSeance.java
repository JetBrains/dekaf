package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



public interface InterSeance {

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

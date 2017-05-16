package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;



public interface InterSeance {

    ////// EXECUTION \\\\\\

    void prepare(InterTask task);

    void assignParameters(Object[] parameters);

    void execute();

    int getAffectedRowsCount();


    ////// CURSORS \\\\\\

    @NotNull
    InterCursor openCursor(byte parameter);


    ////// CLOSING \\\\\\

    void close();

}

package org.jetbrains.dekaf.jdbc.impl;

import org.jetbrains.annotations.NotNull;



public final class JdbcStuff {

    public static void closeIt(@NotNull AutoCloseable closeable) {
        try {
            closeable.close();
        }
        catch (Throwable e) {
            // panic!
            // TODO log somehow
        }
    }

}

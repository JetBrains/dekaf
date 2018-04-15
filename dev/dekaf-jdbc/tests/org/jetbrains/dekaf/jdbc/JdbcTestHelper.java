package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.TaskKind;
import org.jetbrains.dekaf.inter.InterLayout;
import org.jetbrains.dekaf.inter.InterResultKind;
import org.jetbrains.dekaf.inter.InterRowKind;
import org.jetbrains.dekaf.inter.InterTask;

import java.io.Serializable;



public class JdbcTestHelper {

    @Nullable
    protected static String queryString(final @NotNull JdbcFacade facade,
                                        final @NotNull String query) {
        InterLayout layout = new InterLayout(InterResultKind.RES_ONE_ROW,
                                             InterRowKind.ROW_ONE_VALUE,
                                             null,
                                             String.class,
                                             new Class[] { String.class });

        JdbcSession session = facade.openSession();
        JdbcSeance seance = session.openSeance();
        seance.prepare(new InterTask(TaskKind.TASK_QUERY, query));
        seance.execute();
        JdbcCursor cursor = seance.openCursor((byte) 0, layout);
        Serializable portion = cursor.retrievePortion();
        cursor.close();

        if (portion == null) return null;

        assert portion instanceof String;
        return (String)portion;
    }

}

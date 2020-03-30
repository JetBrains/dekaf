package org.jetbrains.dekaf.jdbc.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.inter.exceptions.DBFetchingException;
import org.jetbrains.dekaf.inter.intf.InterLongsCursor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;



public class JdbcLongsCursor extends JdbcBaseCursor implements InterLongsCursor {

    private long defaultValue = 0L;

    protected JdbcLongsCursor(final @NotNull JdbcSeance seance, final @NotNull ResultSet rset) {
        super(seance, rset);
    }

    @Override
    public void setDefaultValue(final long defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override @Nullable
    public long[] fetchPortion() {
        if (end) return null;

        int portionSize = seance.portionSize;

        long[] array = null;
        int k = 0;

        try {
            while (k < portionSize) {
                boolean ok = rset.next();
                if (!ok) break;
                long value = rset.getLong(1);
                if (rset.wasNull()) value = defaultValue;
                if (array == null) array = new long[portionSize];
                array[k++] = value;
            }
        }
        catch (SQLException e) {
            throw new DBFetchingException("Failed to fetch portion from cursor: "+e.getMessage(), e, seance.statementText);
        }

        if (k == 0) {
            end = true;
            close();
            return null;
        }
        else if (k < portionSize) {
            end = true;
            close();
            //noinspection UnnecessaryLocalVariable
            long[] array1 = Arrays.copyOf(array, k);
            return array1;
        }
        else {
            return array;
        }
    }

    @Nullable
    @Override
    public long[] fetchRow() {
        if (end) return null;

        try {
            boolean ok = rset.next();
            if (ok) {
                long value = rset.getLong(1);
                if (rset.wasNull()) value = defaultValue;
                return new long[] { value };
            }
            else {
                return null;
            }
        }
        catch (SQLException e) {
            throw new DBFetchingException("Failed to fetch a row from cursor: "+e.getMessage(), e, seance.statementText);
        }
    }

}

package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBFetchingException;
import org.jetbrains.dekaf.exceptions.UnknownDBException;
import org.jetbrains.dekaf.inter.InterCursor;
import org.jetbrains.dekaf.inter.InterLayout;
import org.jetbrains.dekaf.util.ArrayUtil;
import org.jetbrains.dekaf.util.SerializableMapEntry;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import static org.jetbrains.dekaf.inter.InterResultKind.RES_EXISTENCE;
import static org.jetbrains.dekaf.util.Objects.castTo;



class JdbcCursor implements InterCursor {

    private final @NotNull JdbcSeance seance;

    private final @NotNull InterLayout layout;

    private @Nullable ResultSet rset;

    private boolean active;

    private int portionSize = 100;

    private int columnCount = 0;
    private int fieldCount = 0;

    private int[] indexMapping;
    private JdbcValueGetter<?>[] getters;



    JdbcCursor(final @NotNull JdbcSeance seance,
               final @NotNull InterLayout layout,
               final @Nullable ResultSet rset) {
        this.seance = seance;
        this.layout = layout;
        this.rset = rset;
        this.active = rset != null;
    }

    public void setPortionSize(final int portionSize) {
        this.portionSize = portionSize;
    }

    @Override
    public synchronized @Nullable Serializable retrievePortion() {
        if (!active || rset == null) return null;
        setupOfNecessary();
        switch (layout.resultKind) {
            case RES_EXISTENCE: return retrieveExistence();
            case RES_ONE_ROW: return retrieveOneRow();
            case RES_TABLE: return retrieveTable();
            case RES_PRIMITIVE_ARRAY: return retrievePrimitiveArray();
            default: throw new IllegalStateException("Unknown how to retrieve a " + layout.resultKind);
        }
    }

    private void setupOfNecessary() {
        if (layout.resultKind == RES_EXISTENCE) return;
        if (columnCount == 0 || fieldCount == 0 || indexMapping == null || getters == null) setup();
    }

    private void setup() {
        try {
            assert rset != null;
            ResultSetMetaData md = getMetaData(rset);
            columnCount = md.getColumnCount();

            int n;
            switch (layout.rowKind) {
                case ROW_NONE:
                    n = 0;
                    break;
                case ROW_ONE_VALUE:
                    n = 1;
                    break;
                case ROW_MAP_ENTRY:
                    n = 2;
                    break;
                default:
                    n = layout.cortegeClasses != null
                            ? layout.cortegeClasses.length
                            : layout.columnNames != null
                                ? layout.columnNames.length
                                : columnCount;
            }
            fieldCount = n;

            if (layout.columnNames != null) {
                if (n != layout.columnNames.length) {
                    String msg = "Something goes wrong: n=" + n + " but we have " + layout.columnNames.length + " columns";
                    throw new IllegalStateException(msg);
                }
                indexMapping = new int[n];
                Map<String,Integer> names = getColumnNames(md);
                for (int i = 0; i < n; i++) {
                    String name = layout.columnNames[i];
                    Integer index = names.get(name);
                    indexMapping[i] = index != null ? index.intValue() : Integer.MIN_VALUE;
                }
            }
            else {
                indexMapping = new int[n];
                for (int i = 0; i < n; i++) {
                    indexMapping[i] = i+1;
                }
            }

            getters = new JdbcValueGetter[n];
            for (int i = 0; i < n; i++) {
                int index = indexMapping[i];
                if (index > columnCount) {
                    String message = "Query returned too few columns";
                    throw new DBFetchingException(message, seance.getStatementText());
                }
                int jdbcType = md.getColumnType(index);
                Class<?> desiredClass = layout.cortegeClasses != null
                        ? layout.cortegeClasses[i]
                        : layout.baseClass;
                assert desiredClass != null;
                JdbcValueGetter<?> getter = JdbcValueGetters.of(jdbcType, desiredClass);
                getters[i] = getter;
            }

        }
        catch (SQLException e) {
            columnCount = fieldCount = 0;
            indexMapping = null;
            throw new DBFetchingException("Failed to get metadata: "+e.getMessage(), e, seance.getStatementText());
        }
    }

    @NotNull
    private Map<String,Integer> getColumnNames(final @NotNull ResultSetMetaData md) {
        try {
            int n = md.getColumnCount();
            Map<String,Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            for (int k = 1; k <= n; k++) {
                String name = md.getColumnName(k);
                if (name != null && name.length() > 0) map.put(name, k);
            }
            return map;
        }
        catch (SQLException e) {
            throw new DBFetchingException("Failed to get metadata: "+e.getMessage(), e, seance.getStatementText());
        }
    }

    private Boolean retrieveExistence() {
        boolean existence = moveNext();
        close();
        return existence;
    }

    private Serializable retrieveOneRow() {
        Serializable row = fetchRow();
        close();
        return row;
    }

    @Nullable
    private Serializable[] retrieveTable() {
        Class<?> rowClass = null;
        Object array = null;

        int k = 0;
        while (k < portionSize) {
            Serializable row = fetchRow();
            if (row == null) break;
            if (array == null) {
                rowClass = row.getClass();
                array = Array.newInstance(rowClass, portionSize);
            }
            Array.set(array, k, row);
            k++;
        }

        if (k == 0) return null; // no more data

        if (k < portionSize) {
            Object arrayWithSpaces = array;
            array = Array.newInstance(rowClass, k);
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(arrayWithSpaces, 0, array, 0, k);
        }

        return (Serializable[]) array;
    }

    @Nullable
    private Serializable fetchRow() {
        boolean ok = moveNext();
        if (ok) {
            switch (layout.rowKind) {
                case ROW_NONE: return null;
                case ROW_ONE_VALUE: return fetchOneValue();
                case ROW_OBJECTS: return fetchObjects();
                case ROW_PRIMITIVES: return fetchPrimitives();
                case ROW_MAP_ENTRY: return fetchMapEntry();
                default: throw new IllegalStateException("Unknown how to fetch a " + layout.rowKind);
            }
        }
        else {
            return null;
        }
    }

    @Nullable
    private Serializable fetchOneValue() {
        return getOneValue(0);
    }

    @Nullable
    private Object[] fetchObjects() {
        Object[] row = ArrayUtil.createArrayOf(layout.baseClass, fieldCount);
        for (int i = 0; i < fieldCount; i++) {
            Serializable value = getOneValue(i);
            row[i] = value;
        }
        return row;
    }

    @Nullable
    private Serializable fetchPrimitives() {
        assert layout.baseClass.isPrimitive();
        Object row = Array.newInstance(layout.baseClass, fieldCount);
        for (int i = 0; i < fieldCount; i++) {
            Object value = getOneValue(i);
            Array.set(row, i, value);
        }
        return (Serializable) row;
    }

    private Serializable retrievePrimitiveArray() {
        assert layout.baseClass.isPrimitive();
        Object array = Array.newInstance(layout.baseClass, portionSize);

        int k = 0;
        while (k < portionSize) {
            boolean ok = moveNext();
            if (!ok) break;
            Serializable value = getOneValue(0);
            Array.set(array, k, value);
            k++;
        }

        if (k == 0) return null; // no more data

        if (k < portionSize) {
            Object arrayWithSpaces = array;
            array = Array.newInstance(layout.baseClass, k);
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(arrayWithSpaces, 0, array, 0, k);
        }

        return (Serializable) array;
    }


    @Nullable
    private SerializableMapEntry fetchMapEntry() {
        Serializable key = getOneValue(0);
        Serializable value = getOneValue(1);
        if (key == null || value == null) return null;

        @SuppressWarnings("unchecked")
        SerializableMapEntry entry = new SerializableMapEntry(key, value);

        return entry;
    }

    @Nullable
    private Serializable getOneValue(int fieldIndex) {
        int columnIndex = indexMapping[fieldIndex];
        try {
            //noinspection ConstantConditions
            return (Serializable) getters[fieldIndex].getValue(rset, columnIndex);
        }
        catch (SQLException e) {
            throw new DBFetchingException(e, seance.getStatementText());
        }
    }

    
    private boolean moveNext() {
        try {
            assert rset != null;
            boolean ok = rset.next();
            if (!ok) close();
            return ok;
        }
        catch (SQLException e) {
            close();
            String msg = "Failed to determine whether the result set has rows: " + e.getMessage();
            throw new DBFetchingException(msg, e, seance.getStatementText());
        }
    }

    @Override
    public synchronized void close() {
        if (active) {
            JdbcUtil.close(rset);
            rset = null;
            active = false;
        }
    }

    /// OTHER \\\

    @Override
    public <I> @Nullable I getSpecificService(@NotNull final Class<I> serviceClass,
                                              @NotNull final String serviceName)
            throws ClassCastException
    {
        switch (serviceName) {
            case Names.INTERMEDIATE_SERVICE: return castTo(serviceClass, this);
            case Names.JDBC_RESULT_SET: return castTo(serviceClass, rset);
            case Names.JDBC_METADATA: return rset != null ? castTo(serviceClass, getMetaData(rset)) : null;
            default: throw new IllegalArgumentException("JdbcSeance has no "+serviceName);
        }
    }

    @NotNull
    private static ResultSetMetaData getMetaData(final @NotNull ResultSet rset) {
        try {
            return rset.getMetaData();
        }
        catch (SQLException sqle) {
            throw new UnknownDBException(sqle, "ResultSet.getMetaData()");
        }
    }

}

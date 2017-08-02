package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.util.JavaPrimitiveKind;

import java.io.Serializable;



/**
 * Layout of the query result.
 *
 * <p>
 *     In general, the query result is a table of rows.
 *     This class defines how this table and its rows should look.
 * </p>
 */
public final class InterLayout implements Serializable {

    

    /**
     * How the whole result should look.
     */
    @NotNull
    public final InterResultKind resultKind;

    /**
     * How the row of the table should look.
     */
    @NotNull
    public final InterRowKind rowKind;

    /**
     * If a row is a primitive or an array of primitives,
     * which primitive is to use.
     */
    @Nullable
    public final JavaPrimitiveKind primitiveKind;

    /**
     * If a row is an object of an array of objects,
     * which class of the objects.
     * When the row is a cortege (an array of objects of different types) or a map entry,
     * the base class is the common base class of objects.
     */
    @NotNull
    public final Class<? extends Serializable> baseClass;

    /**
     * The names mapping.
     * If this field is not null, the result column should ne-mapped according to this names.
     */
    @Nullable
    public final String[] columnNames;

    /**
     * When the row kind is a cortege — this array determines classes of elements.
     * When the row kind is a map entry — this array determines the key class (element 0)
     * and the value class (element 1).
     */
    @Nullable
    public final Class[] cortegeClasses;


    public InterLayout(final @NotNull  InterResultKind   resultKind,
                       final @NotNull  InterRowKind      rowKind,
                       final @Nullable JavaPrimitiveKind primitiveKind,
                       final @Nullable Class             baseClass,
                       final @Nullable String[]          columnNames,
                       final @Nullable Class[]           cortegeClasses) {
        this.resultKind = resultKind;
        this.rowKind = rowKind;
        this.primitiveKind = primitiveKind;
        this.baseClass = baseClass != null
                ? baseClass
                : primitiveKind != null
                    ? primitiveKind.primitiveClass
                    : resultKind == InterResultKind.RES_EXISTENCE
                        ? Boolean.class
                        : resultKind == InterResultKind.RES_TEXT
                            ? String.class
                            : Serializable.class;
        this.columnNames = columnNames;
        this.cortegeClasses = cortegeClasses;

        //if (baseClass != null) {
        //    assert baseClass.isPrimitive() || Serializable.class.isAssignableFrom(baseClass) || Externalizable.class.isAssignableFrom(baseClass)
        //            : "The base class must be serializable: " + baseClass.getSimpleName();
        //}
        if (cortegeClasses != null) {
            assert cortegeClasses.length > 0 : "Cortege classes must be specified (here is an empty array)";
            for (Class elementClass : cortegeClasses) {
                if (elementClass != null && !elementClass.isPrimitive()) {
                    //noinspection unchecked
                    assert this.baseClass.isAssignableFrom(elementClass)
                            : "A cortege element class must extend/implement the base one: " + elementClass
                            .getName();
                    assert Serializable.class.isAssignableFrom(elementClass)
                            : "A cortege element class must be serializable: " + elementClass.getName();
                }
            }
        }
    }

    public InterLayout(final @NotNull  InterResultKind   resultKind,
                       final @NotNull  InterRowKind      rowKind,
                       final @Nullable JavaPrimitiveKind primitiveKind,
                       final @Nullable Class             baseClass,
                       final @Nullable Class[]           cortegeClasses) {
        this(resultKind, rowKind, primitiveKind, baseClass, null, cortegeClasses);
    }

    public InterLayout(final @NotNull  InterResultKind   resultKind,
                       final @NotNull  InterRowKind      rowKind,
                       final @Nullable JavaPrimitiveKind primitiveKind,
                       final @Nullable Class             baseClass) {
        this(resultKind, rowKind, primitiveKind, baseClass, null, null);
    }


}

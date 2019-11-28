package org.jetbrains.dekaf.inter.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;



/**
 * Parameter definition.
 */
public final class ParamDef {

    @NotNull
    public final ParamDirection direction;

    @Nullable
    public final ParamType type;

    public final int length;

    @Nullable
    public final Class<?> javaClass;


    public ParamDef(@NotNull final ParamDirection direction,
                    @Nullable final ParamType type,
                    final int length,
                    @Nullable final Class<?> javaClass) {
        this.direction = direction;
        this.type = type;
        this.length = length;
        this.javaClass = javaClass;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParamDef that = (ParamDef) o;
        return this.length == that.length &&
               this.direction == that.direction &&
               this.type == that.type &&
               Objects.equals(this.javaClass, that.javaClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, type, length, javaClass);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(16);
        b.append(direction.name);
        if (type != null) b.append('.').append(type); else b.append("???");
        if (length != 0) b.append('(').append(length).append(')');
        if (javaClass != null) b.append('.').append(javaClass.getSimpleName());
        return b.toString();
    }
}

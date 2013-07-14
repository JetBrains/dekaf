package org.jetbrains.dba.utils;

import org.jetbrains.annotations.NotNull;


/**
 * A couple of non-null objects of the same type.
 * @author Leonid Bushuev from JetBrains
 */
public final class Couple<T>
{
    @NotNull
    public final T a;

    @NotNull
    public final T b;


    public Couple(@NotNull final T a, @NotNull final T b)
    {
        this.a = a;
        this.b = b;
    }


    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Couple couple = (Couple) o;

        if (!a.equals(couple.a)) return false;
        if (!b.equals(couple.b)) return false;

        return true;
    }


    @Override
    public int hashCode()
    {
        int result = a.hashCode();
        result = 31 * result + b.hashCode();
        return result;
    }


    @Override
    public String toString()
    {
        return "(" + a.toString() + "|" + b.hashCode() + ")";
    }
}

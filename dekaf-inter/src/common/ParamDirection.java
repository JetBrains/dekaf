package org.jetbrains.dekaf.inter.common;

public enum ParamDirection {
    
    paramIn    (true,  false, "in"),
    paramInOut (true,  true,  "in-out"),
    paramOut   (false, true,  "out");

    public final boolean isIn;
    public final boolean isOut;
    public final String  name;

    ParamDirection(final boolean isIn, final boolean isOut, final String name) {
        this.isIn = isIn;
        this.isOut = isOut;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

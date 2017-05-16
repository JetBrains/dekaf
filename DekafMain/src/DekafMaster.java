package org.jetbrains.dekaf;

import org.jetbrains.dekaf.core.DBProvider;
import org.jetbrains.dekaf.impl.BaseProvider;



public final class DekafMaster {

    public static final DBProvider provider;


    static {
        provider = new BaseProvider();
    }

}

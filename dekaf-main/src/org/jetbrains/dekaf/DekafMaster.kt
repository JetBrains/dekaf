package org.jetbrains.dekaf

import org.jetbrains.dekaf.core.DBProvider
import org.jetbrains.dekaf.impl.BaseProvider


object DekafMaster {

    @JvmField
    val provider: DBProvider


    init {
        provider = BaseProvider()
    }

}

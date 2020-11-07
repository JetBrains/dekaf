package org.jetbrains.dekaf.main

import org.jetbrains.dekaf.inter.intf.InterFacade
import org.jetbrains.dekaf.inter.settings.Settings
import org.jetbrains.dekaf.main.base.BaseFacade
import org.jetbrains.dekaf.main.base.FactoryLoader
import org.jetbrains.dekaf.main.db.DbFacade
import java.util.concurrent.CopyOnWriteArrayList


object DbMaster {

    private val facades = CopyOnWriteArrayList<InterFacade>()


    @JvmStatic
    fun open(settings: Settings, connect: Boolean = false): DbFacade {
        // inter
        val interFactory = FactoryLoader.selectInterServiceFactory(settings)
        val interFacade = interFactory.createFacade()
        interFacade.init(settings)
        // main
        val dbFacade = BaseFacade()
        dbFacade.setup(interFacade, settings)
        if (connect) dbFacade.connect()
        // ok
        return dbFacade
    }






}
package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.H2db
import org.jetbrains.dekaf.Rdbms
import org.jetbrains.dekaf.core.DBFacade
import org.jetbrains.dekaf.core.DBProvider
import org.jetbrains.dekaf.core.DekafSettingNames
import org.jetbrains.dekaf.core.Settings
import org.jetbrains.dekaf.inter.InterFacade
import org.jetbrains.dekaf.inter.InterProvider
import org.jetbrains.dekaf.util.getClassIfExists
import org.jetbrains.dekaf.util.getDefaultConstructor
import org.jetbrains.dekaf.util.override
import java.lang.IllegalStateException
import java.util.*
import java.util.Collections.unmodifiableList
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.CopyOnWriteArrayList


class BaseProvider: DBProvider {

    /// INTERMEDIATE PROVIDERS \\\

    private val myProviders = CopyOnWriteArrayList<InterProvider>()
    private val myProvidersUnm = unmodifiableList(myProviders)

    override var interProviders: List<InterProvider>
        get() = myProvidersUnm
        set(newProviders) { myProviders.clear(); myProviders.addAll(newProviders) }

    init {
        val providers = ArrayList<InterProvider>()
        val providerClass1 = getClassIfExists<InterProvider>("org.jetbrains.dekaf.jdbc.JdbcProvider")
        if (providerClass1 != null) {
            val constructor1 = providerClass1.getDefaultConstructor()
            val provider1 = constructor1.newInstance()
            providers += provider1
        }
        interProviders = providers
    }



    /// FACADES \\\

    private val facades = ConcurrentLinkedDeque<BaseFacade>()


    override fun provide(rdbms: Rdbms): DBFacade {
        if (myProviders.isEmpty())
            throw IllegalStateException("No intermediate providers")
        for (p in myProviders)
            if (rdbms in p.supportedRdbms())
                return provide(p, rdbms)
        throw RuntimeException("Provider for $rdbms not found")
    }

    override fun provide(connectionString: String): DBFacade {
        if (myProviders.isEmpty())
            throw IllegalStateException("No intermediate providers")
        val autoDescriptor = findDescriptor(connectionString)
        val settings =
                if (autoDescriptor != null)
                    autoDescriptor.defaultSettings.override(mapOf(DekafSettingNames.ConnectionString to connectionString))
                else
                    Settings.NO_SETTINGS

        for (p in myProviders) {
            if (p.supportedConnectionString(connectionString)) {
                if (autoDescriptor != null) {
                    val facade = provide(autoDescriptor.rdbms)
                    facade.setUp(settings)
                    return facade
                }
                else {
                    return provide(p, connectionString)
                }
            }
        }
        throw RuntimeException("""Provider for "$connectionString" not found""")
    }

    override fun provide(interProvider: InterProvider, rdbms: Rdbms): DBFacade {
        val interFacade = interProvider.createFacade(rdbms)
        return makeFacade(interFacade)
    }

    override fun provide(interProvider: InterProvider, connectionString: String): DBFacade {
        val rdbms = H2db.RDBMS // TODO determine
        val interFacade = interProvider.createFacade(rdbms)

        val settings = Settings(DekafSettingNames.ConnectionString, connectionString)

        interFacade.setUp(settings)
        return makeFacade(interFacade)
    }

    private fun makeFacade(interFacade: InterFacade): BaseFacade {
        val facade = BaseFacade(this, interFacade)
        facade.init()
        facades += facade
        return facade
    }
}
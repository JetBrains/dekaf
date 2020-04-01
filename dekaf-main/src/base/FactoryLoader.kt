package org.jetbrains.dekaf.main.base

import org.jetbrains.dekaf.inter.exceptions.DBFactoryException
import org.jetbrains.dekaf.inter.intf.InterServiceFactory
import org.jetbrains.dekaf.inter.settings.Settings
import java.util.*
import java.util.stream.Collectors

object FactoryLoader {

    private const val baseInterServiceFactoryName = "org.jetbrains.dekaf.jdbc.impl.JdbcServiceFactory"

    fun selectInterServiceFactory(settings: Settings): InterServiceFactory {
        val factories = InterFactories.factories
        if (factories.isEmpty()) throw DBFactoryException("No instances of InterServiceFactory")
        var f = factories.firstOrNull { it.javaClass.name == baseInterServiceFactoryName }
        f ?: throw DBFactoryException("No base instance of InterServiceFactory")
        return f
    }


    private object InterFactories {

        val factories: Collection<InterServiceFactory>


        init {
            val loader: ServiceLoader<InterServiceFactory> =
                    ServiceLoader.load(InterServiceFactory::class.java)
            val providers: List<ServiceLoader.Provider<InterServiceFactory>> =
                    loader.stream().collect(Collectors.toList())
            factories = ArrayList(providers.size)
            for (provider in providers) {
                val factory = provider.get()
                // TODO log loading a provider
                factories.add(factory)
            }
        }

    }

}
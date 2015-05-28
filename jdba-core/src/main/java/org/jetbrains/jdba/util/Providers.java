package org.jetbrains.jdba.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;



/**
 * Useful functions for obtaining services.
 * @author Leonid Bushuev from JetBrains
 */
public abstract class Providers {


  /**
   * Loads and returns the first service that implements the given interface.
   * @param providerClass   the desired service class.
   * @param <P>            service class.
   * @return               a service.
   * @throws NoSuchProviderException  services of that class were not found.
   */
  @NotNull
  public static <P> P loadProvider(@NotNull final Class<P> providerClass)
          throws NoSuchProviderException
  {
    final Iterator providersIterator =
            sun.misc.Service.providers(providerClass);
    if (!providersIterator.hasNext()) {
      throw new NoSuchProviderException("A provider named "+providerClass.getName()+" not found.");
    }

    final Object provider = providersIterator.next();
    if (providerClass.isAssignableFrom(provider.getClass())) {
      //noinspection unchecked
      return (P) provider;
    }
    else {
      String msg = String.format("The provider named %s is an instance of %s (not the requested class)",
                                 providerClass.getName(), provider.getClass().getName());
      throw new NoSuchProviderException(msg);
    }
  }


  public static <P> Collection<P> loadAllProviders(@NotNull final Class<P> serviceClass) {
    final Iterator providersIterator =
            sun.misc.Service.providers(serviceClass);

    Collection<P> providers = new LinkedList<P>();
    while (providersIterator.hasNext()) {
      Object p = providersIterator.next();
      if (serviceClass.isAssignableFrom(p.getClass())) {
        //noinspection unchecked
        providers.add((P)p);
      }
    }

    return providers;
  }




  //// EXCEPTIONS \\\\

  public static class NoSuchProviderException extends RuntimeException {
    private NoSuchProviderException(final String message) {
      super(message);
    }
  }

}

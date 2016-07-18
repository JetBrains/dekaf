package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.exceptions.DBFactoryException;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateFacade;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateFederatedProvider;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateRdbmsProvider;
import org.jetbrains.dekaf.intermediate.PrimeIntermediateRdbmsProvider;
import org.jetbrains.dekaf.util.Providers;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public final class JdbcIntermediateFederatedProvider
        implements IntegralIntermediateFederatedProvider {


  //// INNERS STRUCTURES \\\\

  private static final class SpecificProvider {
    @NotNull final Rdbms rdbms;
             final byte specificity;
    @NotNull final IntegralIntermediateRdbmsProvider provider;

    private SpecificProvider(@NotNull final Rdbms rdbms,
                                      final byte specificity,
                             @NotNull final IntegralIntermediateRdbmsProvider provider) {
      this.rdbms = rdbms;
      this.specificity = specificity;
      this.provider = provider;
    }

    @Override
    public String toString() {
      return rdbms + "/" + specificity + " -> " + provider.getClass().getName();
    }
  }


  //// STATE \\\\
  
  private final List<SpecificProvider> myRegisteredProviders = 
          new CopyOnWriteArrayList<SpecificProvider>(); 
  
  private final ConcurrentMap<Rdbms, SpecificProvider> myBestProviders =
          new ConcurrentHashMap<Rdbms, SpecificProvider>();


  //// CONSTRUCTOR \\\\

  public JdbcIntermediateFederatedProvider() {
    // register existent RDBMS providers
    final Collection<IntegralIntermediateRdbmsProvider> rdbmsProviders =
            Providers.loadAllProviders(IntegralIntermediateRdbmsProvider.class, null);
    for (IntegralIntermediateRdbmsProvider rdbmsProvider : rdbmsProviders) {
      registerProvider(rdbmsProvider);
    }
  }
  
  
  //// REGISTERING AND DEREGISTERING \\\\
  
  public void registerProvider(@NotNull final IntegralIntermediateRdbmsProvider provider) {
    Rdbms rdbms = provider.rdbms();
    byte specificity = provider.specificity();
    SpecificProvider sp = new SpecificProvider(rdbms, specificity, provider);
    myRegisteredProviders.add(sp);
    selectBestProvider(rdbms);
  }

  private void selectBestProvider(@NotNull final Rdbms rdbms) {
    SpecificProvider theBest = null;
    for (SpecificProvider sp : myRegisteredProviders) {
      if (sp.rdbms.equals(rdbms)) {
        if (theBest == null) {
          theBest = sp;
        }
        else {
          if (sp.specificity < theBest.specificity) {
            theBest = sp;
          }
        }
      }
    }

    if (theBest != null) {
      myBestProviders.put(rdbms, theBest);
    }
    else {
      myBestProviders.remove(rdbms);
    }
  }

  public void deregisterProvider(@NotNull final PrimeIntermediateRdbmsProvider provider) {
    Rdbms rdbms = provider.rdbms();
    for (int i = myRegisteredProviders.size() - 1; i >= 0; i--) {
      SpecificProvider sp = myRegisteredProviders.get(i);
      if (sp.provider == provider) myRegisteredProviders.remove(i);
    }
    selectBestProvider(rdbms);
  }


  //// INTERFACE IMPLEMENTATION \\\\



  @NotNull
  @Override
  public Set<Rdbms> supportedRdbms() {
    return Collections.unmodifiableSet(myBestProviders.keySet());
  }

  @NotNull
  @Override
  public IntegralIntermediateFacade openFacade(@NotNull final String connectionString,
                                               @Nullable final Properties connectionProperties,
                                               final int connectionsLimit) {
    IntegralIntermediateRdbmsProvider provider = findTheBestFor(connectionString);
    return provider.openFacade(connectionString, connectionProperties, connectionsLimit);
  }

  private IntegralIntermediateRdbmsProvider findTheBestFor(final String connectionString) {
    SpecificProvider theBest = null;
    for (SpecificProvider sp : myBestProviders.values()) {
      if (matches(connectionString, sp.provider.connectionStringPattern())) {
        if (theBest == null || sp.specificity < theBest.specificity) {
          theBest = sp;
        }
      }
    }

    if (theBest != null) {
      return theBest.provider;
    }
    else {
      throw new DBFactoryException(String.format("No providers registered for connection string \"%s\"", connectionString));
    }
  }



  @Nullable
  @Override
  public IntegralIntermediateRdbmsProvider getSpecificServiceProvider(@NotNull final Rdbms rdbms) {
    final SpecificProvider sp = myBestProviders.get(rdbms);
    return sp != null ? sp.provider : null;
  }


  private static boolean matches(@NotNull final String string, @NotNull final Pattern pattern) {
    return pattern.matcher(string).matches();
  }
}

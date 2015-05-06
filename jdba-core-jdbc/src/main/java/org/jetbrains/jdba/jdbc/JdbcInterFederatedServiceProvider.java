package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.core.DBInterFacade;
import org.jetbrains.jdba.core.DBInterFederatedServiceProvider;
import org.jetbrains.jdba.core.DBInterRdbmsServiceProvider;
import org.jetbrains.jdba.core.exceptions.DBFactoryException;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcInterFederatedServiceProvider implements DBInterFederatedServiceProvider {


  //// INNERS STRUCTURES \\\\

  private static final class SpecificProvider {
    @NotNull final Rdbms rdbms;
             final byte specificity;
    @NotNull final DBInterRdbmsServiceProvider provider;

    private SpecificProvider(@NotNull final Rdbms rdbms,
                                      final byte specificity,
                             @NotNull final DBInterRdbmsServiceProvider provider) {
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
  
  public static final JdbcInterFederatedServiceProvider INSTANCE = new JdbcInterFederatedServiceProvider();
  
  
  //// CONSTRUCTOR \\\\

  private JdbcInterFederatedServiceProvider() {
    registerProvider(UnknownDatabaseServiceProvider.INSTANCE);
  }
  
  
  //// REGISTERING AND DEREGISTERING \\\\
  
  public void registerProvider(@NotNull final DBInterRdbmsServiceProvider provider) {
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

  public void deregisterProvider(@NotNull final DBInterRdbmsServiceProvider provider) {
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
  public DBInterFacade openFacade(@NotNull final String connectionString,
                                  @Nullable final Properties connectionProperties,
                                  final int connectionsLimit) {
    DBInterRdbmsServiceProvider provider = findTheBestFor(connectionString);
    return provider.openFacade(connectionString, connectionProperties, connectionsLimit);
  }

  private DBInterRdbmsServiceProvider findTheBestFor(final String connectionString) {
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
  public DBInterRdbmsServiceProvider getSpecificServiceProvider(@NotNull final Rdbms rdbms) {
    final SpecificProvider sp = myBestProviders.get(rdbms);
    return sp != null ? sp.provider : null;
  }


  private static boolean matches(@NotNull final String string, @NotNull final Pattern pattern) {
    return pattern.matcher(string).matches();
  }
}

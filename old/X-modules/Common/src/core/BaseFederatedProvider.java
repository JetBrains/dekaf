package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.intermediate.AdaptIntermediateFederatedProvider;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateFacade;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateFederatedProvider;
import org.jetbrains.dekaf.intermediate.PrimeIntermediateFederatedProvider;
import org.jetbrains.dekaf.util.Providers;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseFederatedProvider implements DBFederatedProvider {


  //// STATE \\\\

  @Nullable
  private IntegralIntermediateFederatedProvider myIntermediateProvider;


  //// INITIALIZATION \\\\

  public void initLocally() {
    myIntermediateProvider = Providers.loadProvider(IntegralIntermediateFederatedProvider.class);
  }

  public void initRemotely(@NotNull final PrimeIntermediateFederatedProvider remoteProvider) {
    myIntermediateProvider = new AdaptIntermediateFederatedProvider(remoteProvider);
  }


  //// IMPLEMENTATION \\\\


  @NotNull
  @Override
  public synchronized Set<Rdbms> supportedRdbms() {
    if (myIntermediateProvider != null) {
      return myIntermediateProvider.supportedRdbms();
    }
    else {
      return Collections.emptySet();
    }
  }

  @NotNull
  @Override
  public synchronized DBFacade openFacade(@NotNull final String connectionString,
                                          @Nullable final Properties connectionProperties,
                                          final int connectionsLimit,
                                          final boolean connect) {
    if (myIntermediateProvider != null) {
      boolean ok = false;
      final IntegralIntermediateFacade intermediateFacade =
              myIntermediateProvider.openFacade(connectionString, connectionProperties, connectionsLimit);
      try {
        final BaseFacade facade = new BaseFacade(intermediateFacade);
        if (connect) {
          facade.connect();
        }
        ok = true;
        return facade;
      }
      finally {
        if (!ok) {
          intermediateFacade.disconnect();
        }
      }
    }
    else {
      throw new IllegalStateException("The federated provider is not initialized yet");
    }
  }
}

package org.jetbrains.dekaf.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class AdaptIntermediateFederatedProvider implements IntegralIntermediateFederatedProvider {

  @NotNull
  private final PrimeIntermediateFederatedProvider myRemoteProvider;


  public AdaptIntermediateFederatedProvider(@NotNull final PrimeIntermediateFederatedProvider remoteProvider) {
    myRemoteProvider = remoteProvider;
  }


  @Override
  @NotNull
  public Set<Rdbms> supportedRdbms() {
    return Collections.unmodifiableSet(myRemoteProvider.supportedRdbms());
  }

  @Override
  @NotNull
  public IntegralIntermediateFacade openFacade(@NotNull final String connectionString,
                                            @Nullable final Properties connectionProperties,
                                            final int connectionsLimit) {
    final PrimeIntermediateFacade remoteFacade =
            myRemoteProvider.openFacade(connectionString, connectionProperties, connectionsLimit);
    return new AdaptIntermediateFacade(remoteFacade);
  }

  @Override
  @Nullable
  public IntegralIntermediateRdbmsProvider getSpecificServiceProvider(@NotNull final Rdbms rdbms) {
    throw new RuntimeException("Not implemented yet"); // TODO implement or remove from the interface
    //return myRemoteProvider.getSpecificServiceProvider(rdbms);
  }


}

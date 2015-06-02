package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;

import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class AdaptIntermediateRdbmsProvider implements IntegralIntermediateRdbmsProvider {

  @NotNull
  private final Rdbms myRdbms;

  @NotNull
  private final PrimeIntermediateRdbmsProvider myRemoteProvider;

  public AdaptIntermediateRdbmsProvider(@NotNull final PrimeIntermediateRdbmsProvider remoteProvider) {
    myRemoteProvider = remoteProvider;
    myRdbms = remoteProvider.rdbms();
  }


  @NotNull
  @Override
  public Rdbms rdbms() {
    return myRdbms;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {return myRemoteProvider.connectionStringPattern();}

  @Override
  public byte specificity() {return myRemoteProvider.specificity();}

  @NotNull
  @Override
  public IntegralIntermediateFacade openFacade(@NotNull final String connectionString,
                                               @Nullable final Properties connectionProperties,
                                               final int connectionsLimit) {
    final PrimeIntermediateFacade remoteFacade = myRemoteProvider.openFacade(connectionString,
                                                                             connectionProperties,
                                                                             connectionsLimit);
    return new AdaptIntermediateFacade(remoteFacade);
  }

  @NotNull
  @Override
  public DBExceptionRecognizer getExceptionRecognizer() {
    // TODO don't use this service from the remote process, use a local one
    return myRemoteProvider.getExceptionRecognizer();
  }


  @Nullable
  @Override
  public <I> I getSpecificService(@NotNull final Class<I> serviceClass,
                                  @NotNull final String serviceName) throws ClassCastException {
    return myRemoteProvider.getSpecificService(serviceClass, serviceName);
  }
}

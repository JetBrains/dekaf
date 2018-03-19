package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateFederatedProvider;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateRdbmsProvider;
import org.junit.Test;
import sun.misc.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcJarProvidersTest {

  @Test
  public void check_federated_provider_service_is_published() {
    Set<IntegralIntermediateFederatedProvider> providers =
        fetchServiceProviders(IntegralIntermediateFederatedProvider.class);

    Object svc = providers.stream().findFirst().get();

    assertThat(svc).isInstanceOf(IntegralIntermediateFederatedProvider.class);
    assertThat(svc).isInstanceOf(JdbcIntermediateFederatedProvider.class);
  }

  @Test
  public void check_unknown_database_provider_service_is_published() {
    Set<IntegralIntermediateRdbmsProvider> providers =
        fetchServiceProviders(IntegralIntermediateRdbmsProvider.class);

    for (IntegralIntermediateRdbmsProvider provider : providers) {
      if (provider instanceof UnknownDatabaseProvider) return; // found
    }

    fail("A provider for unknown database is not found");
  }

  private static <S> Set<S> fetchServiceProviders(@NotNull Class<S> ifc) {
    HashSet<S> providers = new LinkedHashSet<S>();
    Iterator<S> it = Service.providers(ifc);
    while (it.hasNext()) providers.add(it.next());
    return providers;
  }

}

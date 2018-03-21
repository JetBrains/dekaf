package org.jetbrains.dekaf;

import org.jetbrains.dekaf.core.*;
import org.jetbrains.dekaf.intermediate.AdaptIntermediateRdbmsProviderTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * @author Leonid Bushuev
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    // Intermediate Layer
    AdaptIntermediateRdbmsProviderTest.class,
    // Base client functionality
    BaseSessionTest.class,
    BaseQueryRunnerDirectTest.class,
    BaseQueryRunnerPseudoRemoteTest.class,
    BaseFacadeTest.class,
    BaseRdbmsProviderTest.class,
    BaseFederatedProviderTest.class
})
public class JdbcUnitTests {}

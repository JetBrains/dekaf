package org.jetbrains.dekaf.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.Settings;



/**
 * A settable long-life service that can be instantiated, set up, used and shut down.
 */
public interface InterLongService {

    void setUp(final @NotNull Settings settings);

    void shutDown();

}

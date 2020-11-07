package org.jetbrains.dekaf.inter.intf;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.inter.settings.Settings;



/**
 * Represents one database service with several sessions.
 */
public interface InterFacade {

    /**
     * Initializes the connection service.
     *
     * Depend on the implementation, this procedure can create class loader, load jars,
     * ping the target server, initialize JDBC drivers, etc.
     * 
     * @param settings TBD
     */
    void init(@NotNull Settings settings);

    /**
     * Opens a new session to the database service.
     *
     * Every opened session must be closed using the {@link InterSession#close()} method.
     *
     * @return the opened session.
     *
     * @see #openSession(String, Settings)
     * @see InterSession#close()
     */
    @NotNull
    InterSession openSession();

    /**
     * Opens a new session to the database service.
     *
     * Every opened session must be closed using the {@link InterSession#close()} method.
     *
     * @param connectionString      optional another connection string.
     * @param connectionParameters  optional another connection parameters.
     * @return the opened session.
     *
     * @see #openSession()
     * @see InterSession#close()
     */
    @NotNull @ApiStatus.Internal
    InterSession openSession(@Nullable String connectionString,
                             @Nullable Settings connectionParameters);


    /**
     * Number of opened sessions.
     */
    int getSessionsCount();

    /**
     * Closes all open sessions.
     */
    void closeAllSessions();

}

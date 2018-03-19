package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.core.Settings;
import org.jetbrains.dekaf.inter.InterProvider;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jetbrains.dekaf.jdbc.JdbcDescriptor.DESCRIPTORS;



final class JdbcProvider implements InterProvider {

    ////// STATE \\\\\\

    private Settings baseSettings = new Settings();



    ////// LONG SERVICE \\\\\\

    @Override
    public void setUp(final @NotNull Settings settings) {
        baseSettings = settings;
    }

    @Override
    public void shutDown() {
    }




    ////// RDBMS \\\\\\

    @Override
    public @NotNull Set<Rdbms> supportedRdbms() {
        return DESCRIPTORS.keySet();
    }

    @Override
    public boolean supportedConnectionString(final @NotNull String connectionString) {
        for (JdbcDescriptor descriptor : DESCRIPTORS.values())
            if (descriptor.specific.getConnectionStringPattern()
                                   .matcher(connectionString)
                                   .matches()) return true;
        return false;
    }




    ////// FACADES \\\\\\

    @Override
    public @NotNull JdbcFacade createFacade(final @NotNull Rdbms rdbms) {
        if (!(DESCRIPTORS.containsKey(rdbms))) throw new IllegalArgumentException("The RDBMS " + rdbms + " is not supported");
        JdbcDescriptor descriptor = DESCRIPTORS.get(rdbms);
        assert descriptor != null;
        JdbcFacade facade = new JdbcFacade(this, rdbms, descriptor.specific);
        return facade;
    }

    @Override
    public @NotNull JdbcFacade createFacade(final @NotNull String connectionString) {
        final JdbcDescriptor descriptor = findDescriptor(connectionString);
        if (descriptor == null)
            throw new IllegalArgumentException("Cannot determine RDBMS by the connection string: " + connectionString);
        JdbcFacade facade = new JdbcFacade(this, descriptor.rdbms, descriptor.specific);
        return facade;
    }

    @Nullable
    private JdbcDescriptor findDescriptor(final @NotNull String connectionString) {
        for (Map.Entry<Rdbms, JdbcDescriptor> entry : DESCRIPTORS.entrySet()) {
            JdbcDescriptor d = entry.getValue();
            Specific specific = d.specific;
            Pattern pattern = specific.getConnectionStringPattern();
            Matcher matcher = pattern.matcher(connectionString);
            if (matcher.matches()) return d;
        }
        return null;
    }

}

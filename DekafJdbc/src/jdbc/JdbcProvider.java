package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.inter.InterFacade;
import org.jetbrains.dekaf.inter.InterProvider;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jetbrains.dekaf.jdbc.JdbcDescriptor.DESCRIPTORS;



final class JdbcProvider implements InterProvider {

    ////// STATE \\\\\\

    private final JdbcDriverManager driverManager = new JdbcDriverManager();


    ////// RDBMS \\\\\\

    @Override
    public @NotNull Set<Rdbms> supportedRdbms() {
        return DESCRIPTORS.keySet();
    }

    @Override
    public boolean supportedConnectionString(final @NotNull String connectionString) {
        return false;
    }


    ////// DRIVERS \\\\\\

    @Override
    public void setDriverDirectory(final @NotNull String directory) {
        driverManager.setDriverDirectory(directory);
    }

    @Override
    public @NotNull String getDriverDirectory() {
        return driverManager.getDriverDirectory();
    }

    @Override
    public void setDriverJars(final @Nullable Collection<String> jars) {
        driverManager.setDriverJars(jars);
    }

    @Override
    public @Nullable Collection<String> getDriverJars() {
        return driverManager.getDriverJars();
    }



    ////// FACADES \\\\\\

    @Override
    public @NotNull InterFacade createFacade(final @NotNull Rdbms rdbms) {
        if (!(DESCRIPTORS.containsKey(rdbms))) throw new IllegalArgumentException("The RDBMS " + rdbms + " is not supported");
        JdbcDescriptor descriptor = DESCRIPTORS.get(rdbms);
        assert descriptor != null;
        JdbcFacade facade = new JdbcFacade(this, rdbms, descriptor.specific);
        return facade;
    }

    @Override
    public @NotNull InterFacade createFacade(final @NotNull String connectionString) {
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




    public void shutDown() {
        driverManager.deregisterAllDrivers();
    }

}

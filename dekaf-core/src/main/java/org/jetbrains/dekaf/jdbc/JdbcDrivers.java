package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;



/**
 * Singleton class that holds and works with drivers.
 *
 * @author Leonid Bushuev
 **/
public final class JdbcDrivers {

  ////// STATE \\\\\\

  @NotNull
  private static final AtomicReference<Collection<Driver>> ourPreferredDrivers;


  ////// INITIALIZATION \\\\\\

  static {
    ourPreferredDrivers = new AtomicReference<Collection<Driver>>(Collections.<Driver>emptySet());
  }

  private JdbcDrivers() {}


  ////// SET-UP \\\\\\

  public static void setPreferredDrivers(@Nullable final Collection<Driver> drivers) {
    if (drivers != null && !drivers.isEmpty()) {
      Collection<Driver> newDrivers = Collections.unmodifiableList(new ArrayList<Driver>(drivers));
      ourPreferredDrivers.set(newDrivers);
    }
    else {
      ourPreferredDrivers.set(Collections.<Driver>emptySet());
    }
  }


  ////// USING \\\\\\

  @NotNull
  public static Collection<Driver> getPreferredDrivers() {
    return ourPreferredDrivers.get();
  }

  @Nullable
  public static Driver findPreferredDriverFor(@NotNull final String connectionString) {
    final Collection<Driver> drivers = getPreferredDrivers();
    for (Driver driver : drivers) {
      if (isDriverAcceptingConnectionString(driver, connectionString)) return driver;
    }
    return null;
  }

  private static boolean isDriverAcceptingConnectionString(final @NotNull Driver driver,
                                                           final @NotNull String connectionString)
  {
    try {
      return driver.acceptsURL(connectionString);
    }
    catch (SQLException e) {
      // TODO log in debug mode
      return false;
    }
  }

}

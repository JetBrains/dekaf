package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static java.util.Collections.emptySet;



/**
 * Singleton class that holds and works with drivers.
 *
 * @author Leonid Bushuev
 **/
public final class JdbcDrivers {

  ////// STATE \\\\\\

  private static final Object syncObject = new Object();

  @NotNull
  private static ClassLoader ourDriversClassLoader;

  @NotNull
  private static Collection<Driver> ourPreferredDrivers;


  ////// INITIALIZATION \\\\\\

  static {
    synchronized (syncObject) {
      ourDriversClassLoader = Thread.currentThread().getContextClassLoader();
      ourPreferredDrivers = emptySet();
    }
  }

  private JdbcDrivers() {}


  ////// SET-UP \\\\\\



  public static void setPreferredDrivers(@Nullable final ClassLoader classLoader,
                                         @Nullable final Collection<Driver> drivers) {
    synchronized (syncObject) {
      // class loader
      if (classLoader != null) {
        ourDriversClassLoader = classLoader;
      }
      else {
        ourDriversClassLoader = Thread.currentThread().getContextClassLoader();
      }
      // drivers
      if (drivers != null && !drivers.isEmpty()) {
        ourPreferredDrivers = Collections.unmodifiableList(new ArrayList<Driver>(drivers));
      }
      else {
        ourPreferredDrivers = emptySet();
      }
    }
  }


  ////// USING \\\\\\


  @NotNull
  public static ClassLoader getDriversClassLoader() {
    synchronized (syncObject) {
      return ourDriversClassLoader;
    }
  }

  @NotNull
  public static Collection<Driver> getPreferredDrivers() {
    synchronized (syncObject) {
      return ourPreferredDrivers;
    }
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

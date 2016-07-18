package org.jetbrains.dekaf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.BaseFederatedProvider;
import org.jetbrains.dekaf.core.ConnectionInfo;
import org.jetbrains.dekaf.core.DBFacade;
import org.jetbrains.dekaf.jdbc.JdbcDrivers;
import org.jetbrains.dekaf.util.Providers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;



/**
 * @author Leonid Bushuev
 **/
public final class Dekaf {

  private final String[] cmdLineArgs;

  private final ArrayList<String> myJarNamesToLoad = new ArrayList<String>();

  private String myConnectionString = null;
  private ClassLoader myDriversClassLoader = Dekaf.class.getClassLoader();
  private BaseFederatedProvider myProvider = null;
  private DBFacade myFacade = null;

  private int errors = 0;



  private Dekaf(final String[] cmdLineArgs) {
    this.cmdLineArgs = cmdLineArgs;
  }


  public static void main(String[] args) {

    if (args.length == 0) {
      printBanner();
      return;
    }

    Dekaf dekaf = new Dekaf(args);
    try {
      dekaf.run();
    }
    catch (Abort abort) {
      System.exit(-1);
    }
    catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }


  private void run() {
    processArguments();
    loadJars();
    loadDrivers();
    connect();
    printInfo();
  }

  private void processArguments() {
    for (String argX : cmdLineArgs) {
      String arg = argX.trim();
      String argLo = arg.toLowerCase();
      if (argLo.startsWith("jdbc:")) processArgumentJdbc(arg);
      else if (argLo.startsWith("load:")) processArgumentLoad(arg);
      else error("Unexpected argument: %s", arg);
    }

    checkErrors();
  }

  private void processArgumentJdbc(@NotNull final String arg) {
    if (myConnectionString == null) {
      myConnectionString = arg;
    }
    else {
      error("Too many connection string");
    }
  }

  private void processArgumentLoad(@NotNull final String arg) {
    String jarsStr = arg.substring(5);
    String[] jars = jarsStr.split(",");
    for (String jar : jars) {
      if (jar.length() >= 5 && jar.endsWith(".jar")) {
        myJarNamesToLoad.add(jar);
      }
      else {
        error("Unknown how to load '%s'", jar);
      }
    }
  }

  private void loadJars() {
    final ArrayList<URL> jarUrlList = new ArrayList<URL>(myJarNamesToLoad.size());

    // process names
    for (String jarName : myJarNamesToLoad) {
      File jarFile = new File(jarName);
      if (!jarFile.exists()) {
        error("Jar file to load doesn't exist: %s", jarFile.getPath());
        continue;
      }
      if (!jarFile.isFile()) {
        error("Jar file to load is not a file: %s", jarFile.getPath());
        continue;
      }

      try {
        jarUrlList.add(jarFile.toURI().toURL());
      }
      catch (MalformedURLException e) {
        error("Malformed jar name for jar %s (Exception: %s)", jarName, e.getMessage());
      }
    }

    int n = jarUrlList.size();
    if (n == 0) return;
    final URL[] urls = jarUrlList.toArray(new URL[n]);

    // load jars
    try {
      myDriversClassLoader = new URLClassLoader(urls, Dekaf.class.getClassLoader());
    }
    catch (Exception e) {
      error("Failed to load jars. Exception %s with message %s", e.getClass().getSimpleName(), e.getMessage());
    }
  }

  private void loadDrivers() {
    if (myJarNamesToLoad.isEmpty()) return;

    final Collection<Driver> loadedDrivers = Providers.loadAllProviders(Driver.class, myDriversClassLoader);
    ArrayList<Driver> drivers = new ArrayList<Driver>(loadedDrivers.size());
    for (Driver driver : loadedDrivers) {
      if (driver.getClass().getName().equals("sun.jdbc.odbc.JdbcOdbcDriver")) continue;
      driver.getMajorVersion(); // to initialize the class
      //say("Registering driver: %s", driver.getClass().getName());
      try {
        DriverManager.registerDriver(driver);
        drivers.add(driver);
      }
      catch (SQLException e) {
        error("Failed to register driver %s\n exception %s with message %s", driver.getClass().getName(), e.getClass().getName(), e.getMessage());
      }
    }

    JdbcDrivers.setPreferredDrivers(myDriversClassLoader, drivers);
  }

  private void connect() {
    final String connectionString = myConnectionString;
    if (connectionString == null) {
      error("The connection string is not specified");
      throw new Abort();
    }

    myProvider = new BaseFederatedProvider();
    myProvider.initLocally();
    myFacade = myProvider.openFacade(connectionString, null, 1, true);
  }

  private void printInfo() {
    final DBFacade facade = myFacade;
    assert facade != null;

    final ConnectionInfo info = facade.getConnectionInfo();

    say(PRINT_INFO_TEMPLATE,
        info.rdbmsName,
        info.serverVersion,
        info.driverVersion,
        info.databaseName,
        info.schemaName,
        info.userName);
  }

  private static final String PRINT_INFO_TEMPLATE =
      "Connection info:\n" +
      "\tRDBMS:          \t%s\n" +
      "\tServer version: \t%s\n" +
      "\tDriver version: \t%s\n" +
      "\tDatabase name:  \t%s\n" +
      "\tSchema name:    \t%s\n" +
      "\tUser name:      \t%s\n";


  static void printBanner() {
    String banner =
        "Dekaf                                       \n" +
        "Usage: dekaf connection-string [load:jars]  \n";
    say(banner);
  }

  static void say(String template, Object... args) {
    System.out.println(String.format(template, args));
  }

  static void say(String str) {
    System.out.println(str);
  }

  void error(String errTextTemplate, Object... args) {
    error(String.format(errTextTemplate, args));
  }

  void error(String errText) {
    errors++;
    System.err.printf(errText);
  }


  private void checkErrors() throws Abort {
    if (errors > 0) throw new Abort();
  }


  private static class Abort extends RuntimeException {}
}

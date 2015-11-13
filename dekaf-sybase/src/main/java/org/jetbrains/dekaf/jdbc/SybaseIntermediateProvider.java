package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.Sybase;
import org.jetbrains.dekaf.exceptions.DBInitializationException;
import org.jetbrains.dekaf.exceptions.DBPreparingException;

import java.sql.Driver;
import java.util.Properties;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class SybaseIntermediateProvider extends JdbcIntermediateRdbmsProvider {


  //// SETTINGS AND STATE \\\\

  public final static SybaseIntermediateProvider INSTANCE =
          new SybaseIntermediateProvider();


  /**
   * <ul>
   *
   * <li>Open-source jTDS format: <a href="http://jtds.sourceforge.net/faq.html#urlFormat">JTDS Sybase URLs</a></li>
   * <li>SAP jConnector format: <a href="http://infocenter.sybase.com/help/index.jsp?topic=/com.sybase.infocenter.dc01776.1600/doc/html/san1357754914053.html">JTDS Sybase URLs</a></li>
   * </ul>
   */
  static final Pattern SYBASE_CONNECTION_STRING_PATTERN =
          Pattern.compile("^jdbc:(jtds:)?sybase:(Tds:)?.+$", Pattern.CASE_INSENSITIVE);

  static final String SYBASE_JTDS_CONNECTION_STRING_EXAMPLE =
          "jdbc:jtds:sybase://localhost/Testing";

  static final String SYBASE_NATIVE_CONNECTION_STRING_EXAMPLE =
          "jdbc:sybase:Tds://";

  private static final String SYBASE_JTDS_DRIVER_CLASS_NAME =
          "net.sourceforge.jtds.jdbc.Driver";



  //// INITIALIZATION \\\\



  @NotNull
  @Override
  protected String getConnectionStringExample() {
    return SYBASE_JTDS_CONNECTION_STRING_EXAMPLE;
  }

  @Override
  protected Driver loadDriver() {
    Class<Driver> driverClass = getSimpleAccessibleDriverClass(SYBASE_JTDS_DRIVER_CLASS_NAME);
    if (driverClass == null) {
      // TODO try to load from jars
    }
    if (driverClass == null) {
      throw new DBInitializationException("Sybase Driver class not found");
    }

    final Driver driver;
    try {
      driver = driverClass.newInstance();
    }
    catch (Exception e) {
      throw new DBPreparingException("Failed to instantiate driver: "+e.getMessage(), e);
    }

    return driver;
  }


  //// IMPLEMENTATION \\\\



  @NotNull
  @Override
  public Rdbms rdbms() {
    return Sybase.RDBMS;
  }

  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return SYBASE_CONNECTION_STRING_PATTERN;
  }

  @Override
  public byte specificity() {
    return SPECIFICITY_NATIVE;
  }


  @NotNull
  @Override
  protected SybaseIntermediateFacade instantiateFacade(@NotNull final String connectionString,
                                                      @Nullable final Properties connectionProperties,
                                                      final int connectionsLimit,
                                                      @NotNull final Driver driver) {
    return new SybaseIntermediateFacade(connectionString,
                                       connectionProperties,
                                       driver,
                                       connectionsLimit,
                                       SybaseExceptionRecognizer.INSTANCE);
  }

  @NotNull
  @Override
  public BaseExceptionRecognizer getExceptionRecognizer() {
    return SybaseExceptionRecognizer.INSTANCE;
  }

}

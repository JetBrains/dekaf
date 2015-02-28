package org.jetbrains.dba.rdbms.microsoft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.access.DBServiceFactory;
import org.jetbrains.dba.sql.MicrosoftSQL;

import javax.sql.DataSource;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class MicrosoftServiceFactory implements DBServiceFactory {

  private final MicrosoftSQL mySQL = new MicrosoftSQL();
  private final MssqlErrorRecognizer myErrorRecognizer = new MssqlErrorRecognizer();
  private final Pattern myConnectionStringPattern = Pattern.compile("^jdbc(:jtds)?:sqlserver:.*$");


  @NotNull
  @Override
  public Rdbms rdbms() {
    return org.jetbrains.dba.rdbms.microsoft.MicrosoftSQL.RDBMS;
  }


  @NotNull
  @Override
  public MicrosoftSQL cloneSQL() {
    return mySQL.clone();
  }


  @NotNull
  @Override
  public MssqlErrorRecognizer errorRecognizer() {
    return myErrorRecognizer;
  }


  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return myConnectionStringPattern;
  }


  @NotNull
  @Override
  public MssqlFacade createFacade(@NotNull DataSource source) {
    return new MssqlFacade(org.jetbrains.dba.rdbms.microsoft.MicrosoftSQL.RDBMS, source, myErrorRecognizer, cloneSQL());
  }

}

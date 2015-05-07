package org.jetbrains.jdba.microsoft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.core1.DBServiceFactory;

import javax.sql.DataSource;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class MicrosoftServiceFactory implements DBServiceFactory {

  private final MicrosoftTSQL mySQL = new MicrosoftTSQL();
  private final MssqlErrorRecognizer myErrorRecognizer = new MssqlErrorRecognizer();
  private final Pattern myConnectionStringPattern = Pattern.compile("^jdbc(:jtds)?:sqlserver:.*$");


  @NotNull
  @Override
  public Rdbms rdbms() {
    return MicrosoftSQL.RDBMS;
  }


  @NotNull
  @Override
  public MicrosoftTSQL cloneSQL() {
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
    return new MssqlFacade(MicrosoftSQL.RDBMS, source, myErrorRecognizer, cloneSQL());
  }

}

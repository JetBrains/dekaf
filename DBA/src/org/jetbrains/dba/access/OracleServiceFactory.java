package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.KnownRdbms;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.sql.OraSQL;

import javax.sql.DataSource;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class OracleServiceFactory implements DBServiceFactory {

  private final OraSQL mySQL = new OraSQL();
  private final OraErrorRecognizer myErrorRecognizer = new OraErrorRecognizer();
  private final Pattern myConnectionStringPattern = Pattern.compile("^jdbc:oracle:.*$");


  @NotNull
  @Override
  public Rdbms rdbms() {
    return KnownRdbms.ORACLE;
  }


  @NotNull
  @Override
  public OraSQL cloneSQL() {
    return mySQL.clone();
  }


  @NotNull
  @Override
  public OraErrorRecognizer errorRecognizer() {
    return myErrorRecognizer;
  }


  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return myConnectionStringPattern;
  }


  @NotNull
  @Override
  public OraFacade createFacade(@NotNull DataSource source) {
    return new OraFacade(KnownRdbms.ORACLE, source, myErrorRecognizer, cloneSQL());
  }

}

package org.jetbrains.jdba.rdbms.postgre;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.core.DBServiceFactory;
import org.jetbrains.jdba.sql.SQL;

import javax.sql.DataSource;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class PostgreServiceFactory implements DBServiceFactory {

  private final SQL mySQL = new SQL();
  private final PostgreErrorRecognizer myErrorRecognizer = new PostgreErrorRecognizer();
  private final Pattern myConnectionStringPattern = Pattern.compile("^jdbc:postgresql:.*$");


  @NotNull
  @Override
  public Rdbms rdbms() {
    return Postgre.RDBMS;
  }


  @NotNull
  @Override
  public SQL cloneSQL() {
    return mySQL.clone();
  }


  @NotNull
  @Override
  public PostgreErrorRecognizer errorRecognizer() {
    return myErrorRecognizer;
  }


  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return myConnectionStringPattern;
  }


  @NotNull
  @Override
  public PostgreFacade createFacade(@NotNull DataSource source) {
    return new PostgreFacade(Postgre.RDBMS, source, myErrorRecognizer, cloneSQL());
  }

}

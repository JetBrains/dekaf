package org.jetbrains.jdba.rdbms.mysql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.core.DBServiceFactory;
import org.jetbrains.jdba.sql.SQL;

import javax.sql.DataSource;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class MysqlServiceFactory implements DBServiceFactory {

  private final SQL mySQL = new SQL();
  private final MysqlErrorRecognizer myErrorRecognizer = new MysqlErrorRecognizer();
  private final Pattern myConnectionStringPattern = Pattern.compile("^jdbc:mysql:.*$");


  @NotNull
  @Override
  public Rdbms rdbms() {
    return MySQL.RDBMS;
  }


  @NotNull
  @Override
  public SQL cloneSQL() {
    return mySQL.clone();
  }


  @NotNull
  @Override
  public MysqlErrorRecognizer errorRecognizer() {
    return myErrorRecognizer;
  }


  @NotNull
  @Override
  public Pattern connectionStringPattern() {
    return myConnectionStringPattern;
  }


  @NotNull
  @Override
  public MysqlFacade createFacade(@NotNull DataSource source) {
    return new MysqlFacade(MySQL.RDBMS, source, myErrorRecognizer, cloneSQL());
  }

}

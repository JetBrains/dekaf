package org.jetbrains.dba;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.dba.utils.Couple;
import org.junit.experimental.categories.Categories;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import testing.categories.*;

import java.util.Map;



/**
 * Specifies RDBMS-related categories for our tests.
 */
public class RdbmsCategories extends Suite {


    static final Map<Rdbms, Couple<Class<?>>> RDBMS_CATEGORIES =
      ImmutableMap.of(
        org.jetbrains.dba.rdbms.postgre.Postgre.RDBMS, Couple.of(ForPostgre.class, NotForPostgre.class),
        org.jetbrains.dba.rdbms.oracle.Oracle.RDBMS, Couple.of(ForOracle.class, NotForOracle.class),
        org.jetbrains.dba.rdbms.microsoft.MicrosoftSQL.RDBMS, Couple.of(ForMSSQL.class, NotForMSSQL.class),
        org.jetbrains.dba.rdbms.mysql.MySQL.RDBMS, Couple.of(ForMySQL.class, NotForMySQL.class)
      );


    static Class<?> myIncludeCategory;
    static Class<?> myExcludeCategory;


    static {
      Rdbms rdbms = TestEnvironment.RDBMS;
      System.out.printf("Using test categories for %s", rdbms.toString());
      Couple<Class<?>> categories = RDBMS_CATEGORIES.get(rdbms);
      if (categories != null) {
        myIncludeCategory = categories.a;
        myExcludeCategory = categories.b;
      }
      else {
        myIncludeCategory = null;
        myExcludeCategory = null;
      }
    }


    public RdbmsCategories(Class<?> klass, RunnerBuilder builder) throws InitializationError {
      super(klass, builder);
      try {
        filter(new Categories.CategoryFilter(myIncludeCategory, myExcludeCategory));
      }
      catch (NoTestsRemainException e) {
        throw new InitializationError(e);
      }
    }

}
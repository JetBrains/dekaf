package org.jetbrains.dba;

import org.jetbrains.dba.access.JdbcDriverSupportTest;
import org.jetbrains.dba.utils.NumberUtilsTest;
import org.jetbrains.dba.utils.StringsTest;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;

import static org.junit.runners.Suite.SuiteClasses;



@SuiteClasses({
                NumberUtilsTest.class,
                StringsTest.class,
                JdbcDriverSupportTest.class
              })
@RunWith(Categories.class)
public class AllTests {}




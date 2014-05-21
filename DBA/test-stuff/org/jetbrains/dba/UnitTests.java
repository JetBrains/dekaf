package org.jetbrains.dba;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * @author Leonid Bushuev from JetBrains
 */
@RunWith(Categories.class)
@Categories.ExcludeCategory(JDBC.class)
public class UnitTests extends AllTests {}

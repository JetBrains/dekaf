package org.jetbrains.dba.testing;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;



/**
 * @author Leonid Bushuev from JetBrains
 */
@RunWith(Categories.class)
@Categories.ExcludeCategory(JDBC.class)
public class UnitTests extends AllTests {}

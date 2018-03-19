package org.jetbrains.dekaf.junitft;

import org.junit.runner.Description;

import java.lang.reflect.Method;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class SimpleTestMethod extends TestMethod {


  public SimpleTestMethod(Class<?> javaClass, Method javaMethod) {
    super(javaClass, javaMethod);
  }


  @Override
  Description getDescription() {
    return Description.createTestDescription(javaClass, javaMethod.getName());
  }
}

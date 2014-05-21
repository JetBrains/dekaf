package org.jetbrains.dba.junit;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;

import java.lang.reflect.Method;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class TestMethod extends FrameworkMethod {

  protected final Class<?> javaClass;
  protected final Method javaMethod;


  public TestMethod(Class<?> javaClass, Method javaMethod) {
    super(javaMethod);
    this.javaClass = javaClass;
    this.javaMethod = javaMethod;
  }


  abstract Description getDescription();

}

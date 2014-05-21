package org.jetbrains.dba.junit;

import com.google.common.collect.ImmutableList;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class FineRunner extends BlockJUnit4ClassRunner {

  private ArrayList<TestMethod> myJavaMethods = null;


  public FineRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }


  @Override
  protected List<FrameworkMethod> getChildren() {
    if (myJavaMethods == null) {
      final Class<?> klass = getTestClass().getJavaClass();
      Method[] declaredMethods = klass.getDeclaredMethods();
      myJavaMethods = new ArrayList<TestMethod>(declaredMethods.length);

      for (Method method : declaredMethods) {

        // a simple method
        if (method.isAnnotationPresent(Test.class) && !method.isAnnotationPresent(Ignore.class)) {
          SimpleTestMethod m = new SimpleTestMethod(klass, method);
          myJavaMethods.add(m);
        }


      }
    }
    return ImmutableList.<FrameworkMethod>copyOf(myJavaMethods);
  }

}

package org.jetbrains.dekaf.junitft;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class FineRunner extends BlockJUnit4ClassRunner {

  private ArrayList<TestMethod> myTestMethods = null;


  public FineRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }


  @Override
  protected void validateTestMethods(List<Throwable> errors) {

  }


  @Override
  protected List<FrameworkMethod> computeTestMethods() {
    if (myTestMethods == null) {
      final Class<?> klass = getTestClass().getJavaClass();
      Method[] declaredMethods = klass.getDeclaredMethods();
      myTestMethods = new ArrayList<TestMethod>(declaredMethods.length);

      for (Method method : declaredMethods) {

        // a simple method
        if (method.isAnnotationPresent(Test.class) && !method.isAnnotationPresent(Ignore.class)) {
          SimpleTestMethod m = new SimpleTestMethod(klass, method);
          myTestMethods.add(m);
        }

        if (method.isAnnotationPresent(TestWithParams.class) && !method.isAnnotationPresent(Ignore.class)) {
          prepareTestWithParams(klass, method);
        }

      }
    }
    return new ArrayList<FrameworkMethod>(myTestMethods);
  }


  private void prepareTestWithParams(Class<?> klass, Method method) {
    TestWithParams annotation = method.getAnnotation(TestWithParams.class);
    String paramsMemberName = annotation.params();

    try {
      Field paramsField = null;
      for (Class<?> klazz = klass; paramsField == null && klazz != null; klazz = klazz.getSuperclass()) {
        try {
          paramsField = klazz.getDeclaredField(paramsMemberName);
        }
        catch (Exception e) {
          paramsField = null;
        }
      }

      if (paramsField == null) {
        throw new IllegalStateException("Class " + klass.getSimpleName() + " has no field " + paramsMemberName);
      }

      paramsField.setAccessible(true);
      Class<?> paramsFieldType = paramsField.getType();
      if (paramsFieldType.isArray()) {
        Object o = paramsField.get(klass);
        Object[] rows = (Object[]) o;
        for (Object row : rows) {
          ParamTestMethod m = new ParamTestMethod(klass, method, row);
          myTestMethods.add(m);
        }
      }
      else {
        // TODO
      }
    }
    catch (IllegalAccessException e) {
      // TODO
    }
  }


  @Override
  protected Description describeChild(FrameworkMethod method) {
    return method instanceof TestMethod
           ? ((TestMethod)method).getDescription()
           : super.describeChild(method);
  }


  /*
  @Override
  protected void runChild(FrameworkMethod method, RunNotifier notifier) {
    if (method instanceof ParamTestMethod) runParamTestMethod((ParamTestMethod)method, notifier);
    else super.runChild(method, notifier);
  }


  private void runParamTestMethod(ParamTestMethod method, RunNotifier notifier) {
    Statement stmt = new ParamStatement(method);
    runLeaf(stmt, method.getDescription(), notifier);
  }


  class ParamStatement extends Statement {

    private final ParamTestMethod method;


    public ParamStatement(ParamTestMethod method) {
      this.method = method;
    }


    @Override
    public void evaluate() throws Throwable {
      if (method.isArray) {
        Object[] params = (Object[]) method.paramData;
        method.invokeExplosively()
      }
      method.invokeExplosively()
    }
  }
  */
}
